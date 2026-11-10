package interviewmate;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/** 4. 면접 진행 + 8. 돌발 상황 카드 (메뉴 없이 창 전체 사용) */
public class InterviewScreen extends Canvas {
    private List<String> qs;
    private int type, idx, seconds, answered, habit;
    private boolean panel, muted, replaying;
    private int replayLeft;
    private String gazeMsg = "카메라를 봐 주세요", speedMsg = "적당해요";
    private boolean gazeOk = false;
    private int[] surprise = null;      // {카드 번호, 남은 초}
    private int surpriseNo = 0;
    private final Random rnd = new Random();
    private final Timer timer = new Timer(1000, e -> tick());
    // 마이크로 잰 값: 전체 샘플 수, 말한 샘플 수, 침묵 시간(초)
    private int samples, spokenSamples;
    private double silence;

    // 화면 배치 (노트북 기준)
    private static final double SX = 32, SY = 72, SW = 840, SH = 496;   // 면접관 무대
    private static final double RX = 896, RW = 352;                     // 오른쪽 칸

    public InterviewScreen() {
        super(App.W, App.H);
        setBackground(G.DARK);
        // 0.06초마다: 카메라 화면 갱신 + 마이크 측정
        new Timer(60, e -> {
            if (!isShowing() || !timer.isRunning()) return;
            if (Media.micOk && !muted && surprise == null) {
                samples++;
                if (Media.speaking()) { spokenSamples++; silence = 0; }
                else silence += 0.06;
            }
            repaint();
        }).start();
    }

    private boolean micLive() { return Media.micOk && !muted; }

    /** 면접 시작 준비 */
    public void start() {
        if (App.bossMode) {
            type = 4;
            qs = Data.questionsFor(4, 10);
        } else {
            type = App.interviewer;
            qs = App.customQuestions != null ? App.customQuestions : Data.questionsFor(type, App.questionCount);
        }
        idx = 0; seconds = 0; answered = 0; habit = 0; surpriseNo = 0;
        samples = 0; spokenSamples = 0; silence = 0;
        panel = App.bossMode; muted = false; surprise = null;
        timer.restart();
        if (App.tts) speak();
    }

    private void speak() { replaying = true; replayLeft = 2; }

    private void tick() {
        seconds++;
        if (replaying && --replayLeft <= 0) replaying = false;
        // 시선·습관어는 흉내 (실제로는 얼굴인식·음성인식이 필요)
        if (App.realtime && seconds % 3 == 0) {
            gazeOk = rnd.nextInt(3) > 0;
            gazeMsg = gazeOk ? "좋아요, 유지하세요" : "카메라를 봐 주세요";
            String[] sp = {"적당해요", "적당해요", "조금 빨라요", "조금 느려요"};
            speedMsg = sp[rnd.nextInt(sp.length)];
            if (!muted && (!Media.micOk || Media.speaking()) && rnd.nextInt(4) == 0) habit++;
        }
        if (surprise != null && --surprise[1] <= 0) acceptSurprise();
        repaint();
    }

    private void next() {
        if (surprise != null) return;
        answered++;
        if (type == 1) App.progress("press", 1);
        if (idx == 0 && qs.get(0).contains("자기소개")) App.progress("intro", 1);
        idx++;
        if (idx >= qs.size()) { finish(); return; }
        habit = 0;
        silence = 0;
        if (App.tts) speak();
        boolean show = App.bossMode ? (idx == 2 || idx == 6)
                : App.surpriseFreq == 1 ? idx == 2
                : App.surpriseFreq == 2 && idx % 2 == 0;
        if (show) surprise = new int[]{surpriseNo == 0 ? 0 : rnd.nextInt(Data.SURPRISES.length), 30};
    }

    private void acceptSurprise() {
        if (surprise[0] == 0) panel = true;
        surprise = null;
        surpriseNo++;
    }

    private void finish() {
        timer.stop();
        Data.Session s = Data.makeSession(type, qs, App.realtime, answered);
        if (samples > 50) {
            // 마이크로 잰 '말한 시간 비율'로 말 속도 점수 (40~70%가 가장 좋음)
            double ratio = spokenSamples / (double) samples;
            int score = (int) Math.round(95 - Math.abs(ratio - 0.55) * 90);
            s.scores[2] = Math.max(40, Math.min(98, score));
        }
        s.snap = Media.frame;
        App.sessions.add(0, s);
        App.lastSession = s;
        App.progress("mock", 1);
        if (s.scores[0] >= 80) App.progress("gaze", 1);
        App.weekDone[LocalDate.now().getDayOfWeek().getValue() - 1] = true;
        if (App.bossMode) {
            int gauge = Math.min(100, s.total() + answered);
            App.bossBest = Math.max(App.bossBest, gauge);
            if (gauge >= 80 && !App.bossCleared) {
                App.bossCleared = true;
                App.executiveUnlocked = true;
                App.addXp(500);
                App.toast("보스전 클리어! 깐깐한 임원형이 열렸어요");
            } else App.toast("설득 게이지 " + gauge + "% · 합격선 80%");
            App.bossMode = false;
        }
        App.customQuestions = null;
        App.tab("report");
    }

    private void stop() {
        int r = JOptionPane.showConfirmDialog(this, "면접을 종료할까요?\n답변한 문항까지 리포트를 만들어요.",
                "면접 종료", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;
        if (answered == 0) { timer.stop(); App.customQuestions = null; App.bossMode = false; App.tab("home"); }
        else finish();
    }

    @Override protected void draw(Graphics2D g) {
        if (qs == null) return;
        Data.Type t = Data.TYPES[type];
        // 상단 상태 줄
        String name = App.bossMode ? "깐깐한 임원 3인" : t.name;
        int nw = G.width(g, name, G.b(14)) + 46;
        G.rr(g, SX, 20, nw, 36, 18, new Color(0x1E2433));
        G.circle(g, SX + 18, 38, 5, new Color(0xF87171));
        G.text(g, name, SX + 31, 28, G.b(14), new Color(0xE5E7EB));
        G.textC(g, "질문 " + (idx + 1) + " / " + qs.size(), SX + SW / 2, 28, G.b(15), new Color(0xD1D5DB));
        G.rr(g, RX + RW - 128, 20, 128, 36, 18, new Color(0x4A1515));
        if (seconds % 2 == 0) G.circle(g, RX + RW - 110, 38, 5, new Color(0xEF4444));
        G.text(g, String.format("REC %02d:%02d", seconds / 60, seconds % 60), RX + RW - 98, 28, G.b(14), new Color(0xFCA5A5));

        if (panel || (surprise != null && surprise[0] == 0)) drawPanel(g); else drawRoom(g, t);
        drawSelf(g);

        // 실시간 피드백
        if (App.realtime) {
            chip(g, 352, "시선", gazeMsg, gazeOk);
            String voice = speedMsg;
            if (micLive()) {    // 마이크로 실제 측정
                if (silence >= 3) voice = "침묵 " + (int) silence + "초";
                else if (Media.speaking() && Media.level < 0.12f) voice = "조금 작아요";
                else voice = "적당해요";
            }
            chip(g, 428, micLive() ? "목소리" : "말 속도", voice, voice.equals("적당해요"));
            chip(g, 504, "습관어", "“음…” " + habit + "회", habit < 3);
        } else {
            G.rr(g, RX, 352, RW, 216, 16, new Color(0x161B26));
            G.monitor(g, RX + 24, 380, new Color(0x93B4FA));
            G.text(g, "녹화 후 피드백 모드", RX + 54, 378, G.b(15), G.WHITE);
            G.wrap(g, "면접에만 집중하세요. 끝나면 녹화 영상과 함께 문항별 리포트로 알려 드려요.",
                    RX + 24, 418, RW - 48, G.f(13), new Color(0x9CA3AF), 21);
        }

        // 질문 카드
        G.rr(g, SX, 584, SW, 120, 18, G.DARK_CARD);
        G.wrap(g, qs.get(idx), SX + 26, 606, 640, G.b(19), G.WHITE, 27);
        String hint = muted ? "마이크가 꺼져 있어요" : replaying ? "질문을 읽는 중…"
                : Media.speaking() ? "말하는 중…" : "답변을 시작하세요";
        G.text(g, hint, SX + 26, 672, G.f(13), Media.speaking() && !muted ? G.GREEN_BRIGHT : new Color(0x9CA3AF));
        G.rrLine(g, SX + SW - 172, 652, 150, 40, 20, new Color(0x4B5563), 1.2f);
        G.speaker(g, SX + SW - 152, 672, G.WHITE);
        G.text(g, "질문 다시 듣기", SX + SW - 130, 662, G.b(13), G.WHITE);
        hit(SX + SW - 172, 652, 150, 40, this::speak);

        // 오른쪽 아래 버튼
        boolean last = idx == qs.size() - 1;
        G.rr(g, RX, 584, RW, 56, 14, G.BLUE);
        G.textBox(g, last ? "답변 완료 · 리포트 보기" : "답변 완료 · 다음 질문", RX, 584, RW, 56, G.b(16), G.WHITE);
        hit(RX, 584, RW, 56, this::next);
        G.circle(g, RX + 28, 676, 28, new Color(0x1E2433));
        if (micLive() && Media.level > 0.02f)
            G.ring(g, RX + 28, 676, 28 + Media.level * 7, new Color(74, 222, 128, 90 + (int) (Media.level * 160)), 2.5f);
        G.mic(g, RX + 28, 676, G.WHITE, muted);
        hit(RX, 648, 56, 56, () -> { muted = !muted; App.toast(muted ? "마이크를 껐어요" : "마이크를 켰어요"); });
        G.rr(g, RX + 72, 648, RW - 72, 56, 14, new Color(0x2A1414));
        G.textBox(g, "면접 종료", RX + 72, 648, RW - 72, 56, G.b(15), new Color(0xFCA5A5));
        hit(RX + 72, 648, RW - 72, 56, this::stop);

        if (surprise != null) drawSurprise(g);
    }

    private void chip(Graphics2D g, double y, String label, String value, boolean ok) {
        G.rr(g, RX, y, RW, 64, 14, ok ? new Color(0x14261A) : new Color(0x2E2016));
        G.text(g, label, RX + 20, y + 23, G.f(13), new Color(0x9CA3AF));
        G.textR(g, value, RX + RW - 20, y + 21, G.b(16), ok ? G.GREEN_BRIGHT : G.ORANGE_BRIGHT);
    }

    /** 면접실 배경 + 면접관 1명. 휴대폰 버전 그림을 무대 크기에 맞게 키워서 그림 */
    private void drawRoom(Graphics2D g, Data.Type t) {
        RoundRectangle2D stage = new RoundRectangle2D.Double(SX, SY, SW, SH, 36, 36);
        Shape old = g.getClip();
        AffineTransform at = g.getTransform();
        g.clip(stage);
        double sc = SH / 353.0;
        g.translate(SX + (SW - 362 * sc) / 2, SY);
        g.scale(sc, sc);
        g.translate(0, -89);
        G.rr(g, -300, 89, 962, 353, 0, new Color(0x2A3249));
        // 창문
        G.rr(g, 219, 123, 113, 139, 4, new Color(0x3A4566));
        G.rr(g, 225, 129, 46, 60, 0, new Color(0x45527A));
        G.rr(g, 278, 129, 48, 60, 0, new Color(0x45527A));
        G.rr(g, 225, 195, 46, 62, 0, new Color(0x45527A));
        G.rr(g, 278, 195, 48, 62, 0, new Color(0x45527A));
        // 선반, 책, 화분
        G.rr(g, -40, 197, 150, 5, 2, new Color(0x5A4636));
        G.rr(g, -25, 161, 11, 36, 1, new Color(0xA0613B));
        G.rr(g, -12, 169, 9, 28, 1, new Color(0x6A7AA8));
        G.rr(g, 0, 158, 13, 39, 1, new Color(0xB9A47C));
        G.circle(g, 80, 180, 13, new Color(0x4C8A55));
        G.rr(g, 73, 186, 15, 11, 1, new Color(0x8A5A3C));
        // 면접관
        Faces.person(g, 181, 272, 43, new Color(0x1F2937), t.hair, t.expr, t.glasses, true, 400);
        // 책상과 명패
        G.rr(g, -300, 388, 962, 54, 0, new Color(0x473A30));
        G.rr(g, -300, 388, 962, 5, 0, new Color(0x5A4A3C));
        G.rr(g, 139, 403, 84, 24, 3, new Color(0xE5E7EB));
        G.textBox(g, "면접관", 139, 403, 84, 24, G.b(11.5f), G.INK);
        g.setTransform(at);
        g.setClip(old);
    }

    /** 3:1 면접 화면 */
    private void drawPanel(Graphics2D g) {
        RoundRectangle2D box = new RoundRectangle2D.Double(SX, SY, SW, SH, 36, 36);
        g.setPaint(new GradientPaint(0, (float) SY, new Color(0x1C2233), 0, (float) (SY + SH), new Color(0x232A3B)));
        g.fill(box);
        Shape old = g.getClip();
        g.clip(box);
        double bottom = SY + SH;
        Faces.person(g, SX + SW / 6, SY + 300, 48, new Color(0x161B26), Faces.HAIR, Faces.Expr.FLAT, false, false, bottom);
        Faces.person(g, SX + SW / 2, SY + 300, 48, new Color(0x334155), Faces.GRAY_HAIR,
                App.bossMode ? Faces.Expr.FROWN : Faces.Expr.FLAT, false, false, bottom);
        Faces.person(g, SX + SW * 5 / 6, SY + 300, 48, new Color(0x4A3A2C), new Color(0x3B2A1C), Faces.Expr.FLAT, false, false, bottom);
        g.setClip(old);
    }

    /** 내 카메라 */
    private void drawSelf(Graphics2D g) {
        double y = SY, h = 264;
        RoundRectangle2D pip = new RoundRectangle2D.Double(RX, y, RW, h, 32, 32);
        g.setColor(new Color(0x2A3247));
        g.fill(pip);
        if (!Media.draw(g, pip, RX, y, RW, h)) {
            Shape old = g.getClip();
            g.clip(pip);
            Faces.silhouette(g, RX + RW / 2, y + 110, 36, new Color(0x6B7280));
            g.setClip(old);
        }
        G.rrLine(g, RX + RW / 2 - 60, y + 40, 120, 120, 14,
                gazeOk || !App.realtime ? new Color(0x4ADE80) : G.ORANGE_BRIGHT, 2f);
        G.rr(g, RX + 14, y + h - 36, 28, 24, 6, new Color(0x111827));
        G.textBox(g, "나", RX + 14, y + h - 36, 28, 24, G.b(12), G.WHITE);
        if (!Media.camOk)
            G.text(g, Media.camStatus, RX + 52, y + h - 32, G.f(12), new Color(0x9CA3AF));
    }

    private void drawSurprise(Graphics2D g) {
        String[] c = Data.SURPRISES[surprise[0]];
        G.rr(g, 0, 0, App.W, App.H, 0, new Color(13, 17, 23, 170));
        double x = 390, y = 120, w = 500, pad = 32;
        G.rr(g, x, y, w, 500, 26, new Color(0xFFF5EC));
        G.bolt(g, x + pad, y + 32, 15, G.ORANGE);
        G.text(g, "돌발 상황  " + (surpriseNo + 1) + "  /  5", x + pad + 24, y + 31, G.b(13), G.ORANGE);
        double rcx = x + w - pad - 24, rcy = y + 44;
        G.ring(g, rcx, rcy, 24, new Color(0xF4D3C0), 4f);
        g.setColor(G.ORANGE);
        g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Arc2D.Double(rcx - 24, rcy - 24, 48, 48, 90, 360 * surprise[1] / 30.0, Arc2D.OPEN));
        G.textBox(g, String.valueOf(surprise[1]), rcx - 24, rcy - 24, 48, 48, G.b(15), G.ORANGE);

        G.text(g, c[0], x + pad, y + 84, G.b(23), G.INK);
        int h = G.wrap(g, c[1], x + pad, y + 132, w - pad * 2, G.f(14.5f), G.TEXT, 23);
        double ty = y + 132 + h + 16;
        java.util.List<String> tip = G.split(g, c[2], w - pad * 2 - 90, G.f(13.5f));
        double th = tip.size() * 21 + 28;
        G.rr(g, x + pad, ty, w - pad * 2, th, 13, G.WHITE);
        G.text(g, "대응 팁", x + pad + 16, ty + 14, G.b(13.5f), G.BLUE);
        for (int i = 0; i < tip.size(); i++) G.text(g, tip.get(i), x + pad + 80, ty + 14 + i * 21, G.f(13.5f), G.TEXT);

        double by = y + 360;
        G.rr(g, x + pad, by, 120, 52, 14, G.WHITE);
        G.rrLine(g, x + pad, by, 120, 52, 14, G.LINE, 1.2f);
        G.textBox(g, "다른 카드", x + pad, by, 120, 52, G.b(15), G.INK);
        hit(x + pad, by, 120, 52, () -> surprise[0] = (surprise[0] + 1 + rnd.nextInt(Data.SURPRISES.length - 1)) % Data.SURPRISES.length);
        G.rr(g, x + pad + 132, by, w - pad * 2 - 132, 52, 14, G.ORANGE);
        G.textBox(g, "받아들이고 답변하기", x + pad + 132, by, w - pad * 2 - 132, 52, G.b(16), G.WHITE);
        hit(x + pad + 132, by, w - pad * 2 - 132, 52, this::acceptSurprise);

        // 돌발 빈도
        double fy = y + 436;
        G.text(g, "돌발 빈도", x + pad, fy + 11, G.b(13), G.SLATE);
        double sx = x + pad + 80, sw = w - pad * 2 - 80;
        G.rr(g, sx, fy, sw, 42, 12, new Color(0xF1E4D8));
        String[] f = {"없음", "가끔", "자주"};
        double cw = (sw - 8) / 3;
        for (int i = 0; i < 3; i++) {
            double cx = sx + 4 + i * cw;
            if (App.surpriseFreq == i) {
                G.rr(g, cx, fy + 4, cw, 34, 9, G.WHITE);
                G.textBox(g, f[i], cx, fy + 4, cw, 34, G.b(13.5f), G.INK);
            } else G.textBox(g, f[i], cx, fy + 4, cw, 34, G.b(13.5f), G.SLATE);
            final int k = i;
            hit(cx, fy, cw, 42, () -> App.surpriseFreq = k);
        }
    }
}
