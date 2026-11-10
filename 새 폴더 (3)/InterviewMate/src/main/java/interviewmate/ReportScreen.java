package interviewmate;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** 5. 면접 리포트 */
public class ReportScreen extends Page {
    private int selQ = 2;
    private Data.Session shown;

    public ReportScreen() { super("report", G.BG, 100, App.H, 760); }

    @Override public void onShow() {
        if (shown != App.lastSession) {
            shown = App.lastSession;
            selQ = Math.min(2, shown.fb.size() - 1);
            scrollTop();
        }
    }

    @Override protected void drawFixed(Graphics2D g) {
        Data.Session s = App.lastSession;
        App.header(g, this, "면접 리포트", Data.TYPES[s.type].name + " · " + s.count + "문항 · " + s.modeName()
                + " · " + s.when, G.INK, G.SUB);
        G.tag(g, "예시 데이터", 932, 38, 26, G.b(12), new Color(0xE5E7EB), G.TEXT, 11, 8);
    }

    @Override protected void drawBody(Graphics2D g, Body b) {
        Data.Session s = App.lastSession;
        int n = s.fb.size();

        // ===== 왼쪽: 녹화 영상 =====
        double y = 8;
        G.rr(g, 40, y, 560, 380, 20, G.NAVY);
        RoundRectangle2D vid = new RoundRectangle2D.Double(60, y + 20, 520, 292, 24, 24);
        g.setColor(new Color(0x232B3B));
        g.fill(vid);
        if (!Media.draw(g, s.snap, vid, 60, y + 20, 520, 292)) {
            Shape old = g.getClip();
            g.clip(vid);
            Faces.silhouette(g, 320, y + 160, 48, new Color(0x4B5563));
            g.setClip(old);
        }
        G.circle(g, 320, y + 166, 36, new Color(0xF3F4F6));
        G.play(g, 323, y + 166, 20, G.INK);
        b.on(284, y + 130, 72, 72, () -> App.toast(App.saveVideo ? "녹화 영상 재생은 예시 화면이에요" : "영상 저장을 꺼서 다시 볼 수 없어요"));
        G.tag(g, stamp(selQ) + " · Q" + (selQ + 1), 76, y + 36, 26, G.b(12.5f), new Color(0x111827), G.WHITE, 9, 7);

        double ty = y + 336;
        G.rr(g, 60, ty - 2, 520, 4, 2, new Color(0x374151));
        int dots = Math.min(5, n);
        double step = 520.0 / Math.max(1, dots);
        G.rr(g, 60, ty - 2, step * (selQ + 0.5), 4, 2, G.BLUE_LIGHT);
        for (int i = 0; i < dots; i++) {
            double dx = 60 + step * (i + 0.5);
            boolean good = (s.scores[(i + 1) % 5] + i * 7) % 2 == 0 || i == 0;
            G.circle(g, dx, ty, i == selQ ? 9 : 7.5, G.NAVY);
            G.circle(g, dx, ty, i == selQ ? 7.5 : 6, good ? new Color(0x60A5FA) : G.ORANGE_BRIGHT);
            final int k = i;
            b.on(dx - 14, ty - 14, 28, 28, () -> selQ = k);
        }
        G.circle(g, 66, y + 360, 5.5, G.ORANGE_BRIGHT);
        G.text(g, "개선 포인트", 78, y + 351, G.f(12.5f), new Color(0xD1D5DB));
        G.circle(g, 162, y + 360, 5.5, new Color(0x60A5FA));
        G.text(g, "잘한 장면", 174, y + 351, G.f(12.5f), new Color(0xD1D5DB));

        // ===== 왼쪽: 문항별 피드백 =====
        double cardTop = y + 396;
        Data.QFeedback f = s.fb.get(selQ);
        double cardH = 112 + G.split(g, "“" + f.q + "”", 516, G.b(16)).size() * 25 + 12
                + boxH(g, f.good) + 10 + boxH(g, f.fix) + 22;
        G.rr(g, 40, cardTop, 560, cardH, 20, G.WHITE);
        G.text(g, "문항별 피드백", 62, cardTop + 22, G.b(16), G.INK);
        double qy = cardTop + 58;
        double tw2 = Math.min(90, (516 - (n - 1) * 8) / (double) n);
        for (int i = 0; i < n; i++) {
            double tx = 62 + i * (tw2 + 8);
            boolean on = i == selQ;
            if (on) G.rr(g, tx, qy, tw2, 38, 10, G.INK);
            else { G.rr(g, tx, qy, tw2, 38, 10, G.WHITE); G.rrLine(g, tx, qy, tw2, 38, 10, G.LINE, 1.2f); }
            G.textBox(g, "Q" + (i + 1), tx, qy, tw2, 38, G.b(13.5f), on ? G.WHITE : G.TEXT);
            final int k = i;
            b.on(tx, qy, tw2, 38, () -> selQ = k);
        }
        qy += 54;
        qy += G.wrap(g, "“" + f.q + "”", 62, qy, 516, G.b(16), G.INK, 25) + 12;
        qy = box(g, qy, "잘한 점", f.good, G.GREEN_SOFT, G.GREEN);
        box(g, qy + 10, "고칠 점", f.fix, G.CREAM, G.ORANGE);
        double leftBottom = cardTop + cardH;

        // ===== 오른쪽: 태도 분석 =====
        double rx = 620;
        G.rr(g, rx, y, 400, 250, 20, G.WHITE);
        G.text(g, "태도 분석", rx + 24, y + 24, G.b(16), G.INK);
        String total = String.valueOf(s.total());
        int sw = G.width(g, "/ 100", G.f(14));
        G.textR(g, "/ 100", rx + 376, y + 26, G.f(14), G.SUB);
        int tw = G.width(g, total, G.b(26));
        G.text(g, total, rx + 376 - sw - 6 - tw, y + 12, G.b(26), G.BLUE);
        G.textR(g, "종합", rx + 376 - sw - 12 - tw, y + 26, G.f(14), G.SUB);
        for (int i = 0; i < 5; i++) {
            double ry = y + 86 + i * 34;
            int v = s.scores[i];
            G.text(g, Data.SCORE_NAMES[i], rx + 24, ry - 10, G.f(14), G.TEXT);
            G.bar(g, rx + 120, ry - 4, 200, 8, v / 100.0, v < 70 ? G.ORANGE : G.BLUE, G.TRACK);
            G.textR(g, String.valueOf(v), rx + 376, ry - 10, G.b(14), G.INK);
        }

        // 면접관 속마음 입구
        double ey = y + 266;
        G.rr(g, rx, ey, 400, 80, 20, G.NAVY);
        Faces.person(g, rx + 40, ey + 38, 15, new Color(0x334155), Faces.HAIR, Faces.Expr.FROWN, false, false, ey + 80);
        G.text(g, "면접관 속마음 보기", rx + 76, ey + 18, G.b(15), G.WHITE);
        G.text(g, "그 순간 면접관은 무슨 생각을 했을까요?", rx + 76, ey + 44, G.f(12.5f), new Color(0x9CA3AF));
        G.arrowRight(g, rx + 360, ey + 40, 14, G.WHITE, 2f);
        b.on(rx, ey, 400, 80, () -> App.go("thoughts"));

        // 퀘스트 보상 / 다시 연습
        double by = ey + 96;
        Data.Quest q = App.quest("mock");
        if (q.done() && !q.claimed) {
            G.rr(g, rx, by, 400, 56, 14, G.ORANGE);
            String label = "퀘스트 보상 받기 · +" + q.reward + " XP";
            int lw = G.width(g, label, G.b(16));
            double lx = rx + 200 - (lw + 24) / 2.0;
            G.flag(g, lx + 2, by + 19, 17, G.WHITE, 2f);
            G.text(g, label, lx + 24, by + 17, G.b(16), G.WHITE);
            b.on(rx, by, 400, 56, () -> { q.claimed = true; App.addXp(q.reward); App.toast("+" + q.reward + " XP를 받았어요"); });
        } else {
            App.button(g, null, rx, by, 400, 56, "같은 설정으로 다시 연습하기", G.BLUE, G.WHITE, null);
            b.on(rx, by, 400, 56, () -> {
                App.interviewer = s.type;
                App.realtime = s.realtime;
                App.customQuestions = null;
                App.bossMode = false;
                App.questionCount = s.count == 3 || s.count == 10 ? s.count : 5;
                ((InterviewScreen) App.screen("interview")).start();
                App.go("interview");
            });
        }
        double rightBottom = by + 56;

        b.height((int) Math.max(leftBottom, rightBottom) + 30);
    }

    private double boxH(Graphics2D g, String text) {
        return Math.max(46, G.split(g, text, 430, G.f(13.5f)).size() * 21 + 24);
    }

    private double box(Graphics2D g, double y, String title, String text, Color bg, Color fg) {
        java.util.List<String> lines = G.split(g, text, 430, G.f(13.5f));
        double h = boxH(g, text);
        G.rr(g, 62, y, 516, h, 12, bg);
        G.text(g, title, 78, y + 13, G.b(13), fg);
        for (int i = 0; i < lines.size(); i++) G.text(g, lines.get(i), 136, y + 12 + i * 21, G.f(13.5f), G.TEXT);
        return y + h;
    }

    private String stamp(int q) {
        int sec = q == 2 ? 271 : 31 + q * 127;
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }
}
