package interviewmate;

import java.awt.*;
import java.awt.geom.*;

/** 7. 면접관 속마음 보기 */
public class ThoughtsScreen extends Page {
    private int sel = 2;
    private static final Color CARD = new Color(0x1C2230);

    public ThoughtsScreen() { super("thoughts", G.DARK, 100, App.H, 660); }

    private static Color moodColor(int lvl) {
        if (lvl <= 1) return G.GREEN_BRIGHT;
        if (lvl == 2) return new Color(0xD1D5DB);
        return G.ORANGE_BRIGHT;
    }
    private static Faces.Expr moodFace(int lvl) {
        if (lvl == 0) return Faces.Expr.GRIN;
        if (lvl == 1) return Faces.Expr.SMILE;
        if (lvl == 2) return Faces.Expr.FLAT;
        return Faces.Expr.FROWN;
    }

    @Override protected void drawFixed(Graphics2D g) {
        App.header(g, this, "면접관 속마음 보기", "면접관이 그 순간 무슨 생각을 했는지 들여다봐요", G.WHITE, new Color(0x9CA3AF));
    }

    @Override protected void drawBody(Graphics2D g, Body b) {
        Data.Thought t = Data.THOUGHTS[sel];

        // ===== 왼쪽: 면접관 얼굴 + 말풍선 =====
        double x = 40, y = 8, w = 500, h = 330;
        RoundRectangle2D box = new RoundRectangle2D.Double(x, y, w, h, 40, 40);
        g.setColor(CARD);
        g.fill(box);
        Shape old = g.getClip();
        g.clip(box);
        Faces.person(g, x + 170, y + 150, 56, new Color(0x1E293B), Faces.HAIR, moodFace(t.moodLevel), false, false, y + h);
        g.setClip(old);
        java.util.List<String> lines = G.split(g, t.text, 180, G.b(14));
        double bh = lines.size() * 22 + 24;
        G.rr(g, x + 270, y + 30, 210, bh, 12, new Color(0xF8FAFC));
        for (int i = 0; i < lines.size(); i++) G.text(g, lines.get(i), x + 284, y + 42 + i * 22, G.b(14), G.INK);
        G.tag(g, t.time, x + 18, y + h - 42, 26, G.b(12.5f), new Color(0x0B0F17), G.WHITE, 10, 8);
        Color mc = moodColor(t.moodLevel);
        int mw = G.width(g, t.mood, G.b(12.5f)) + 20;
        G.tag(g, t.mood, x + w - 18 - mw, y + h - 42, 26, G.b(12.5f),
                t.moodLevel == 3 ? new Color(0x3A1A10) : new Color(0x14261A), mc, 10, 8);

        // 호감도 그래프
        double gy = y + h + 16, gh = 170;
        G.rr(g, x, gy, w, gh, 20, CARD);
        G.text(g, "면접관 호감도 흐름", x + 22, gy + 20, G.f(13.5f), new Color(0xD1D5DB));
        int gw = G.width(g, "좋음", G.b(13.5f));
        G.text(g, "좋음", x + w - 22 - gw, gy + 20, G.b(13.5f), G.GREEN_BRIGHT);
        G.textR(g, "첫인상 7초 · ", x + w - 22 - gw, gy + 20, G.f(13.5f), new Color(0xD1D5DB));
        double base = gy + 104;
        g.setColor(new Color(0x4B5563));
        g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, new float[]{3, 3}, 0));
        g.draw(new Line2D.Double(x + 22, base, x + w - 22, base));
        double[] xs = new double[5], ys = new double[5];
        double[] lv = {-34, -18, 2, 34};
        for (int i = 0; i < 5; i++) {
            xs[i] = x + 70 + i * 92;
            ys[i] = base + lv[Data.THOUGHTS[i].moodLevel];
        }
        Path2D p = new Path2D.Double();
        double px = x + 22, py = base - 8;
        p.moveTo(px, py);
        for (int i = 0; i <= 5; i++) {
            double nx = i < 5 ? xs[i] : x + w - 22, ny = i < 5 ? ys[i] : base - 36;
            double mx = (px + nx) / 2;
            p.curveTo(mx, py, mx, ny, nx, ny);
            px = nx; py = ny;
        }
        g.setColor(G.BLUE_LIGHT);
        g.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(p);
        for (int i = 0; i < 5; i++) {
            Color c = moodColor(Data.THOUGHTS[i].moodLevel);
            double r = i == sel ? 9 : 6;
            G.circle(g, xs[i], ys[i], r + 1.5, CARD);
            G.circle(g, xs[i], ys[i], r, c);
            G.textC(g, Data.THOUGHTS[i].time, xs[i], gy + gh - 28, G.f(11.5f), i == sel ? G.WHITE : new Color(0x6B7280));
            final int k = i;
            b.on(xs[i] - 20, ys[i] - 20, 40, 40, () -> sel = k);
        }

        // ===== 오른쪽: 순간별 속마음 목록 =====
        double rx = 560, rw = 460, ry = y;
        for (int i = 0; i < Data.THOUGHTS.length; i++) {
            Data.Thought th = Data.THOUGHTS[i];
            java.util.List<String> ls = G.split(g, th.text, rw - 90, G.b(14));
            double ih = ls.size() * 22 + 46;
            boolean on = i == sel;
            G.rr(g, rx, ry, rw, ih, 16, on ? new Color(0x1F2840) : CARD);
            if (on) G.rrLine(g, rx, ry, rw, ih, 16, G.BLUE_LIGHT, 1.4f);
            G.text(g, th.time, rx + 16, ry + 16, G.b(12.5f), new Color(0x9CA3AF));
            for (int k = 0; k < ls.size(); k++) G.text(g, ls.get(k), rx + 70, ry + 14 + k * 22, G.b(14), G.WHITE);
            G.text(g, th.mood + " · " + th.reason, rx + 70, ry + 18 + ls.size() * 22, G.f(12.5f), moodColor(th.moodLevel));
            final int k = i;
            b.on(rx, ry, rw, ih, () -> sel = k);
            ry += ih + 10;
        }
        b.height((int) Math.max(ry, gy + gh) + 30);
    }
}
