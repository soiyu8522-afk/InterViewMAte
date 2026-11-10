package interviewmate;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** 3. 피드백 방식 선택 (2/2 단계) + 카메라·마이크 확인 */
public class FeedbackModeScreen extends Page {
    public FeedbackModeScreen() {
        super("feedback", G.BG, 100, App.H - 86, 560);
        // 카메라 화면이 움직이도록 자주 다시 그림
        new javax.swing.Timer(60, e -> { if (isShowing()) body.repaint(); }).start();
    }

    @Override protected void drawFixed(Graphics2D g) {
        App.header(g, this, "피드백은 언제 받을까요?", "2 / 2 단계 · 면접 중 내 모습은 화면 녹화로 분석해요", G.INK, G.SLATE);
        G.line(g, 0, App.H - 86, App.CW, App.H - 86, G.TRACK, 1);
        G.text(g, "면접관", 40, App.H - 66, G.f(12.5f), G.SUB);
        G.text(g, (App.bossMode ? "깐깐한 임원 3인" : Data.TYPES[App.interviewer].name) + " · "
                + (App.realtime ? "실시간 피드백" : "녹화 후 피드백"), 40, App.H - 46, G.b(15), G.INK);
        G.rr(g, 760, App.H - 70, 260, 50, 13, G.BLUE);
        String label = "녹화하며 면접 시작";
        int w = G.width(g, label, G.b(15));
        double x = 890 - (w + 18) / 2.0;
        G.circle(g, x + 4, App.H - 45, 5, G.WHITE);
        G.text(g, label, x + 18, App.H - 56, G.b(15), G.WHITE);
        hit(760, App.H - 70, 260, 50, () -> {
            ((InterviewScreen) App.screen("interview")).start();
            App.go("interview");
        });
    }

    @Override protected void drawBody(Graphics2D g, Body b) {
        // 왼쪽: 피드백 방식 + 설정
        option(g, b, 12, true, "실시간 피드백", "면접 중",
                "시선·말 속도·자세를 화면에 바로 알려줘요. 습관을 즉시 고치고 싶을 때 좋아요.");
        option(g, b, 130, false, "녹화 후 피드백", "면접 후",
                "면접에만 집중하고, 끝나면 녹화 영상과 함께 문항별 상세 리포트를 받아요.");
        G.rr(g, 40, 252, 500, 180, 16, G.WHITE);
        row(g, b, 262, "질문을 음성으로 읽어주기", "면접관이 직접 말하듯 질문해요", App.tts, () -> App.tts = !App.tts);
        G.line(g, 60, 318, 520, 318, G.TRACK, 1);
        row(g, b, 320, "녹화 영상 저장", "다시 보기와 비교 리포트에 사용해요", App.saveVideo, () -> App.saveVideo = !App.saveVideo);
        G.line(g, 60, 376, 520, 376, G.TRACK, 1);
        row(g, b, 378, "질문 자막 표시", "음성과 함께 화면에 질문을 띄워요", App.subtitle, () -> App.subtitle = !App.subtitle);

        // 오른쪽: 카메라·마이크 확인
        double x = 560, y = 12, w = 460;
        G.rr(g, x, y, w, 420, 16, G.WHITE);
        G.text(g, "카메라·마이크 확인", x + 22, y + 20, G.b(16), G.INK);
        G.text(g, "얼굴과 어깨가 프레임 안에 들어오게 맞춰 주세요.", x + 22, y + 46, G.f(13), G.SUB);
        RoundRectangle2D cam = new RoundRectangle2D.Double(x + 22, y + 78, w - 44, 250, 24, 24);
        g.setColor(new Color(0x1F2937));
        g.fill(cam);
        if (!Media.draw(g, cam, x + 22, y + 78, w - 44, 250)) {
            Shape old = g.getClip();
            g.clip(cam);
            Faces.silhouette(g, x + w / 2, y + 180, 34, new Color(0x4B5563));
            g.setClip(old);
        }
        G.dashed(g, x + w / 2 - 70, y + 110, 140, 150, 60, new Color(255, 255, 255, 110));  // 얼굴 위치 안내선
        Color camCol = Media.camOk ? G.GREEN : G.ORANGE;
        if (Media.camOk) G.check(g, x + 24, y + 349, 11, camCol, 2.2f);
        else G.circle(g, x + 29, y + 355, 4, camCol);
        G.text(g, "카메라 · " + Media.camStatus, x + 42, y + 344, G.b(13), camCol);
        G.mic(g, x + 29, y + 389, Media.micOk ? G.TEXT : G.SUB, false);
        if (Media.micOk) {
            G.bar(g, x + 46, y + 386, 300, 7, Math.min(1, Media.level * 1.4), Media.speaking() ? G.GREEN : G.BLUE_LIGHT, G.TRACK);
            G.text(g, Media.speaking() ? "잘 들려요" : "말해 보세요", x + 360, y + 380, G.f(12.5f), G.SUB);
        } else G.text(g, "마이크 · " + Media.micStatus, x + 46, y + 381, G.b(13), G.ORANGE);
        b.height(450);
    }

    private void option(Graphics2D g, Body b, double y, boolean isRealtime, String title, String tag, String desc) {
        boolean sel = App.realtime == isRealtime;
        if (sel) G.rr(g, 37, y - 3, 506, 112, 18, new Color(0xDCE4FB));
        G.rr(g, 40, y, 500, 106, 16, G.WHITE);
        if (sel) G.rrLine(g, 40, y, 500, 106, 16, G.BLUE, 2f);
        if (isRealtime) {
            G.rr(g, 60, y + 20, 46, 46, 11, G.ORANGE_SOFT);
            G.pulse(g, 73, y + 35, G.ORANGE);
        } else {
            G.rr(g, 60, y + 20, 46, 46, 11, G.BLUE_SOFT);
            G.monitor(g, 74, y + 34, G.BLUE);
        }
        int tw = G.text(g, title, 122, y + 20, G.b(17), G.INK);
        G.pill(g, tag, 122 + tw + 10, y + 21, 22, G.b(11), new Color(0xF1F2F4), G.TEXT, 9);
        G.wrap(g, desc, 122, y + 52, 360, G.f(13), G.SUB, 19);
        G.radio(g, 512, y + 30, 10.5, sel);
        b.on(40, y, 500, 106, () -> App.realtime = isRealtime);
    }

    private void row(Graphics2D g, Body b, double y, String title, String sub, boolean on, Runnable toggle) {
        G.text(g, title, 62, y + 10, G.b(14.5f), G.INK);
        G.text(g, sub, 62, y + 32, G.f(12), G.SUB);
        G.checkbox(g, 494, y + 16, 22, on);
        b.on(40, y, 500, 56, toggle);
    }
}
