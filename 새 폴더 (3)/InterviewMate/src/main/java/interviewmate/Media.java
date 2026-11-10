package interviewmate;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * 카메라 미리보기 + 마이크 소리 크기.
 * - 카메라: webcam-capture 라이브러리로 프레임을 받아 둠 (frame)
 * - 마이크: 자바 기본 기능(javax.sound)으로 소리 크기(level, 0~1)를 계산
 * 둘 다 없거나 실패하면 앱은 그대로 동작하고, 그림/예시 데이터로 대신합니다.
 */
public final class Media {
    private Media() {}

    public static volatile BufferedImage frame;   // 가장 최근 카메라 화면
    public static volatile float level;           // 마이크 소리 크기 0~1
    public static volatile boolean camOk, micOk;
    public static volatile String camStatus = "카메라 준비 중…";
    public static volatile String micStatus = "마이크 준비 중…";

    /** 말하고 있다고 볼 소리 크기 */
    public static final float SPEAK = 0.06f;

    private static volatile boolean active;      // 지금 카메라·마이크를 써야 하는지
    private static Thread camThread, micThread;

    /** 카메라·마이크 켜기 (스레드는 한 번만 만들고 계속 재사용) */
    public static synchronized void start() {
        active = true;
        if (camThread == null) {
            camThread = new Thread(Media::camLoop, "camera");
            camThread.setDaemon(true);
            camThread.start();
            micThread = new Thread(Media::micLoop, "mic");
            micThread.setDaemon(true);
            micThread.start();
        }
    }

    /** 카메라·마이크 끄기 (장치를 놓아 줌) */
    public static synchronized void stop() {
        active = false;
        frame = null;
        level = 0;
    }

    public static boolean speaking() { return micOk && level > SPEAK; }

    private static void nap(long ms) { try { Thread.sleep(ms); } catch (InterruptedException ignore) {} }

    // ===== 카메라 =====
    private static void camLoop() {
        CamGrabber cam;
        try {
            cam = new CamGrabber();       // 라이브러리가 없으면 여기서 NoClassDefFoundError
        } catch (Throwable e) {
            camOk = false;
            camStatus = "카메라 라이브러리가 없어요";
            return;
        }
        boolean open = false, tried = false;
        while (true) {
            try {
                if (active) {
                    if (!open && !tried) {
                        tried = true;
                        camStatus = "카메라 켜는 중…";
                        open = cam.open();
                        camOk = open;
                        camStatus = open ? "권한 허용됨" : "카메라를 찾지 못했어요";
                    }
                    if (open) {
                        BufferedImage img = cam.grab();
                        if (img != null && active) frame = img;
                    }
                } else {
                    if (open) { cam.close(); open = false; }
                    tried = false;          // 다음에 켤 때 다시 시도
                    frame = null;
                }
            } catch (NoClassDefFoundError e) {
                camOk = false;
                camStatus = "카메라 라이브러리가 없어요";
                return;                     // 라이브러리 없이 실행됨 → 카메라 기능 끔
            } catch (Throwable e) {
                camOk = false;
                open = false;
                camStatus = "카메라를 열 수 없어요";
                System.err.println("카메라 오류: " + e);
            }
            nap(50);                        // 약 20fps
        }
    }

    // ===== 마이크 =====
    private static void micLoop() {
        AudioFormat fmt = new AudioFormat(16000f, 16, 1, true, false);
        TargetDataLine line = null;
        byte[] buf = new byte[1600];        // 0.05초 분량
        boolean tried = false;
        while (true) {
            try {
                if (active) {
                    if (line == null && !tried) {
                        tried = true;
                        line = AudioSystem.getTargetDataLine(fmt);
                        line.open(fmt, 3200);
                        line.start();
                        micOk = true;
                        micStatus = "마이크 연결됨";
                    }
                    if (line != null) {
                        int n = line.read(buf, 0, buf.length);
                        double sum = 0;
                        int count = 0;
                        for (int i = 0; i + 1 < n; i += 2) {
                            int smp = (short) ((buf[i + 1] << 8) | (buf[i] & 0xff));
                            sum += (double) smp * smp;
                            count++;
                        }
                        double rms = count == 0 ? 0 : Math.sqrt(sum / count) / 32768.0;
                        float v = (float) Math.min(1.0, Math.sqrt(rms) * 1.6);   // 작은 소리도 보이게
                        level = active ? level * 0.6f + v * 0.4f : 0;           // 부드럽게
                        continue;                                                // read 가 알아서 기다림
                    }
                } else {
                    if (line != null) { line.stop(); line.close(); line = null; }
                    tried = false;
                    level = 0;
                }
            } catch (Throwable e) {
                micOk = false;
                micStatus = "마이크를 찾지 못했어요";
                if (line != null) try { line.close(); } catch (Throwable ignore) {}
                line = null;
            }
            nap(50);
        }
    }

    /**
     * 카메라 화면을 (x,y,w,h) 칸에 꽉 차게, 거울처럼 좌우 반전해서 그림.
     * 화면이 없으면 false 를 돌려줌 (그럼 실루엣을 대신 그리면 됨)
     */
    public static boolean draw(Graphics2D g, Shape clip, double x, double y, double w, double h) {
        return draw(g, frame, clip, x, y, w, h);
    }

    public static boolean draw(Graphics2D g, BufferedImage img, Shape clip, double x, double y, double w, double h) {
        if (img == null) return false;
        Shape old = g.getClip();
        g.clip(clip);
        double s = Math.max(w / img.getWidth(), h / img.getHeight());
        double dw = img.getWidth() * s, dh = img.getHeight() * s;
        AffineTransform at = new AffineTransform();
        at.translate(x + (w + dw) / 2, y + (h - dh) / 2);
        at.scale(-s, s);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, at, null);
        g.setClip(old);
        return true;
    }
}
