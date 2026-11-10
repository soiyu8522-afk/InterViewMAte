package interviewmate;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/** 색상·글꼴·그리기 도우미 모음 (목업 좌표를 그대로 옮기기 위해 직접 그립니다) */
public final class G {
    private G() {}

    // ===== 색상 =====
    public static final Color BG = new Color(0xF2F3F5);
    public static final Color WHITE = Color.WHITE;
    public static final Color INK = new Color(0x111827);      // 제목 검정
    public static final Color TEXT = new Color(0x374151);
    public static final Color SUB = new Color(0x6B7280);       // 회색 설명
    public static final Color LINE = new Color(0xD1D5DB);
    public static final Color TRACK = new Color(0xE5E7EB);
    public static final Color BLUE = new Color(0x2449D8);
    public static final Color BLUE_SOFT = new Color(0xE6ECFD);
    public static final Color BLUE_LIGHT = new Color(0x93B4FA);
    public static final Color ORANGE = new Color(0xC2410C);
    public static final Color ORANGE_SOFT = new Color(0xFFEAD5);
    public static final Color ORANGE_BRIGHT = new Color(0xFB923C);
    public static final Color CREAM = new Color(0xFFF5EC);
    public static final Color RED = new Color(0xB91C1C);
    public static final Color RED_SOFT = new Color(0xFDE2E2);
    public static final Color GREEN = new Color(0x15803D);
    public static final Color GREEN_SOFT = new Color(0xDCFCE7);
    public static final Color GREEN_BRIGHT = new Color(0x86EFAC);
    public static final Color YELLOW = new Color(0xF59E0B);
    public static final Color YELLOW_SOFT = new Color(0xFEF3C7);
    public static final Color NAVY = new Color(0x111827);
    public static final Color DARK = new Color(0x0D1117);
    public static final Color DARK_CARD = new Color(0x1C2230);
    public static final Color DARK_CARD2 = new Color(0x232A3A);
    public static final Color SLATE = new Color(0x4B5563);

    // ===== 글꼴 =====
    private static String family;
    public static String family() {
        if (family == null) {
            String[] wanted = {"Pretendard", "Malgun Gothic", "맑은 고딕", "Apple SD Gothic Neo",
                    "Noto Sans CJK KR", "Noto Sans KR", "NanumGothic", "나눔고딕"};
            String[] all = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            family = Font.DIALOG;
            outer:
            for (String w : wanted) for (String a : all) if (a.equalsIgnoreCase(w)) { family = a; break outer; }
        }
        return family;
    }
    public static Font f(float size) { return new Font(family(), Font.PLAIN, 1).deriveFont(size); }
    public static Font b(float size) { return new Font(family(), Font.BOLD, 1).deriveFont(size); }

    public static void smooth(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    // ===== 도형 =====
    public static void rr(Graphics2D g, double x, double y, double w, double h, double r, Color c) {
        g.setColor(c);
        g.fill(new RoundRectangle2D.Double(x, y, w, h, r * 2, r * 2));
    }
    public static void rrLine(Graphics2D g, double x, double y, double w, double h, double r, Color c, float sw) {
        g.setColor(c);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(sw));
        g.draw(new RoundRectangle2D.Double(x + sw / 2, y + sw / 2, w - sw, h - sw, r * 2, r * 2));
        g.setStroke(old);
    }
    public static void dashed(Graphics2D g, double x, double y, double w, double h, double r, Color c) {
        g.setColor(c);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, new float[]{4, 3}, 0));
        g.draw(new RoundRectangle2D.Double(x, y, w, h, r * 2, r * 2));
        g.setStroke(old);
    }
    public static void circle(Graphics2D g, double cx, double cy, double r, Color c) {
        g.setColor(c);
        g.fill(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
    }
    public static void ring(Graphics2D g, double cx, double cy, double r, Color c, float sw) {
        g.setColor(c);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(sw));
        g.draw(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
        g.setStroke(old);
    }
    public static void line(Graphics2D g, double x1, double y1, double x2, double y2, Color c, float sw) {
        g.setColor(c);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(x1, y1, x2, y2));
        g.setStroke(old);
    }
    public static void poly(Graphics2D g, Color c, float sw, double... xy) {
        Path2D p = new Path2D.Double();
        p.moveTo(xy[0], xy[1]);
        for (int i = 2; i < xy.length; i += 2) p.lineTo(xy[i], xy[i + 1]);
        g.setColor(c);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(p);
        g.setStroke(old);
    }

    /** 진행 막대 (0~1) */
    public static void bar(Graphics2D g, double x, double y, double w, double h, double ratio, Color fill, Color track) {
        rr(g, x, y, w, h, h / 2, track);
        if (ratio > 0) rr(g, x, y, Math.max(h, w * Math.min(1, ratio)), h, h / 2, fill);
    }

    // ===== 글자 (y = 글자 상단 기준) =====
    public static int text(Graphics2D g, String s, double x, double y, Font f, Color c) {
        g.setFont(f);
        g.setColor(c);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, (float) x, (float) (y + fm.getAscent()));
        return fm.stringWidth(s);
    }
    public static int width(Graphics2D g, String s, Font f) { return g.getFontMetrics(f).stringWidth(s); }
    public static void textR(Graphics2D g, String s, double right, double y, Font f, Color c) {
        text(g, s, right - width(g, s, f), y, f, c);
    }
    public static void textC(Graphics2D g, String s, double cx, double y, Font f, Color c) {
        text(g, s, cx - width(g, s, f) / 2.0, y, f, c);
    }
    /** 사각형 정가운데에 글자 */
    public static void textBox(Graphics2D g, String s, double x, double y, double w, double h, Font f, Color c) {
        FontMetrics fm = g.getFontMetrics(f);
        double ty = y + (h - (fm.getAscent() + fm.getDescent())) / 2.0;
        textC(g, s, x + w / 2, ty, f, c);
    }

    /** 글자 단위 줄바꿈 (한국어용). 반환값 = 그린 높이 */
    public static int wrap(Graphics2D g, String s, double x, double y, double maxW, Font f, Color c, double lineH) {
        List<String> lines = split(g, s, maxW, f);
        for (int i = 0; i < lines.size(); i++) text(g, lines.get(i), x, y + i * lineH, f, c);
        return (int) Math.round(lines.size() * lineH);
    }
    public static List<String> split(Graphics2D g, String s, double maxW, Font f) {
        FontMetrics fm = g.getFontMetrics(f);
        List<String> out = new ArrayList<>();
        for (String para : s.split("\n")) {
            StringBuilder cur = new StringBuilder();
            int lastSpace = -1;
            for (int i = 0; i < para.length(); i++) {
                char ch = para.charAt(i);
                cur.append(ch);
                if (ch == ' ') lastSpace = cur.length() - 1;
                if (fm.stringWidth(cur.toString()) > maxW) {
                    // 단어 중간이면 마지막 공백에서 자르기 (너무 앞이면 글자 단위)
                    if (lastSpace > cur.length() / 2) {
                        String keep = cur.substring(0, lastSpace);
                        String rest = cur.substring(lastSpace + 1);
                        out.add(keep);
                        cur = new StringBuilder(rest);
                    } else {
                        cur.setLength(cur.length() - 1);
                        out.add(cur.toString());
                        cur = new StringBuilder(String.valueOf(ch));
                    }
                    lastSpace = -1;
                }
            }
            out.add(cur.toString());
        }
        return out;
    }

    /** 둥근 알약 모양 라벨. 반환값 = 너비 */
    public static int pill(Graphics2D g, String s, double x, double y, double h, Font f, Color bg, Color fg, double padX) {
        int w = (int) (width(g, s, f) + padX * 2);
        rr(g, x, y, w, h, h / 2, bg);
        textBox(g, s, x, y, w, h, f, fg);
        return w;
    }
    public static int tag(Graphics2D g, String s, double x, double y, double h, Font f, Color bg, Color fg, double padX, double r) {
        int w = (int) (width(g, s, f) + padX * 2);
        rr(g, x, y, w, h, r, bg);
        textBox(g, s, x, y, w, h, f, fg);
        return w;
    }

    // ===== 아이콘 =====
    public static void back(Graphics2D g, double x, double y, Color c) {
        poly(g, c, 2.2f, x + 7, y, x, y + 7, x + 7, y + 14);
    }
    public static void arrowRight(Graphics2D g, double x, double y, double len, Color c, float sw) {
        line(g, x, y, x + len, y, c, sw);
        poly(g, c, sw, x + len - 6, y - 6, x + len, y, x + len - 6, y + 6);
    }
    public static void check(Graphics2D g, double x, double y, double s, Color c, float sw) {
        poly(g, c, sw, x, y + s * 0.5, x + s * 0.38, y + s * 0.85, x + s, y + s * 0.1);
    }
    public static void checkbox(Graphics2D g, double x, double y, double s, boolean on) {
        if (on) {
            rr(g, x, y, s, s, 3, BLUE);
            check(g, x + s * 0.22, y + s * 0.25, s * 0.56, WHITE, 2.6f);
        } else {
            rr(g, x, y, s, s, 3, WHITE);
            rrLine(g, x, y, s, s, 3, new Color(0x6B7280), 1.5f);
        }
    }
    public static void radio(Graphics2D g, double cx, double cy, double r, boolean on) {
        if (on) {
            circle(g, cx, cy, r, BLUE);
            circle(g, cx, cy, r * 0.38, WHITE);
        } else {
            ring(g, cx, cy, r - 1, LINE, 2f);
        }
    }
    public static void flame(Graphics2D g, double cx, double cy, double s, Color c) {
        Path2D p = new Path2D.Double();
        p.moveTo(cx, cy - s);
        p.curveTo(cx + s * 0.2, cy - s * 0.4, cx + s * 0.8, cy - s * 0.2, cx + s * 0.7, cy + s * 0.35);
        p.curveTo(cx + s * 0.6, cy + s * 0.9, cx - s * 0.6, cy + s * 0.9, cx - s * 0.7, cy + s * 0.35);
        p.curveTo(cx - s * 0.75, cy - s * 0.05, cx - s * 0.4, cy - s * 0.15, cx - s * 0.3, cy - s * 0.45);
        p.curveTo(cx - s * 0.1, cy - s * 0.2, cx + s * 0.1, cy - s * 0.4, cx, cy - s);
        p.closePath();
        g.setColor(c);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke((float) Math.max(1.6, s * 0.22), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(p);
        g.setStroke(old);
    }
    public static void flag(Graphics2D g, double x, double y, double s, Color c, float sw) {
        line(g, x, y, x, y + s, c, sw);
        poly(g, c, sw, x, y, x + s * 0.75, y, x + s * 0.6, y + s * 0.25, x + s * 0.75, y + s * 0.5, x, y + s * 0.5);
    }
    public static void home(Graphics2D g, double x, double y, double s, Color c, float sw) {
        Path2D p = new Path2D.Double();
        p.moveTo(x, y + s * 0.42);
        p.lineTo(x + s / 2, y);
        p.lineTo(x + s, y + s * 0.42);
        p.lineTo(x + s * 0.88, y + s * 0.42);
        p.lineTo(x + s * 0.88, y + s);
        p.lineTo(x + s * 0.62, y + s);
        p.lineTo(x + s * 0.62, y + s * 0.68);
        p.lineTo(x + s * 0.38, y + s * 0.68);
        p.lineTo(x + s * 0.38, y + s);
        p.lineTo(x + s * 0.12, y + s);
        p.lineTo(x + s * 0.12, y + s * 0.42);
        p.closePath();
        g.setColor(c);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(p);
        g.setStroke(old);
    }
    public static void video(Graphics2D g, double x, double y, double s, Color c, float sw) {
        g.setColor(c);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new RoundRectangle2D.Double(x, y + s * 0.15, s * 0.68, s * 0.7, 5, 5));
        g.setStroke(old);
        poly(g, c, sw, x + s * 0.68, y + s * 0.42, x + s, y + s * 0.22, x + s, y + s * 0.78, x + s * 0.68, y + s * 0.58);
    }
    public static void chart(Graphics2D g, double x, double y, double s, Color c, float sw) {
        line(g, x, y + s, x + s, y + s, c, sw);
        line(g, x + s * 0.2, y + s * 0.35, x + s * 0.2, y + s * 0.85, c, sw);
        line(g, x + s * 0.5, y, x + s * 0.5, y + s * 0.85, c, sw);
        line(g, x + s * 0.8, y + s * 0.5, x + s * 0.8, y + s * 0.85, c, sw);
    }
    public static void play(Graphics2D g, double cx, double cy, double s, Color c) {
        Path2D p = new Path2D.Double();
        p.moveTo(cx - s * 0.35, cy - s * 0.5);
        p.lineTo(cx + s * 0.55, cy);
        p.lineTo(cx - s * 0.35, cy + s * 0.5);
        p.closePath();
        g.setColor(c);
        g.fill(p);
    }
    public static void mic(Graphics2D g, double cx, double cy, Color c, boolean muted) {
        g.setColor(c);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new RoundRectangle2D.Double(cx - 4, cy - 10, 8, 13, 8, 8));
        g.draw(new Arc2D.Double(cx - 8, cy - 7, 16, 14, 180, 180, Arc2D.OPEN));
        g.draw(new Line2D.Double(cx, cy + 7, cx, cy + 10));
        if (muted) {
            g.setColor(new Color(0xF87171));
            g.draw(new Line2D.Double(cx - 9, cy - 11, cx + 9, cy + 10));
        }
        g.setStroke(old);
    }
    public static void speaker(Graphics2D g, double x, double cy, Color c) {
        Path2D p = new Path2D.Double();
        p.moveTo(x, cy - 3);
        p.lineTo(x + 3.5, cy - 3);
        p.lineTo(x + 8, cy - 7);
        p.lineTo(x + 8, cy + 7);
        p.lineTo(x + 3.5, cy + 3);
        p.lineTo(x, cy + 3);
        p.closePath();
        g.setColor(c);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(p);
        g.draw(new Arc2D.Double(x + 6, cy - 5, 8, 10, -60, 120, Arc2D.OPEN));
        g.setStroke(old);
    }
    public static void bolt(Graphics2D g, double x, double y, double s, Color c) {
        poly(g, c, 1.8f, x + s * 0.6, y, x + s * 0.1, y + s * 0.58, x + s * 0.5, y + s * 0.58, x + s * 0.4, y + s,
                x + s * 0.9, y + s * 0.42, x + s * 0.5, y + s * 0.42, x + s * 0.6, y);
    }
    public static void link(Graphics2D g, double x, double y, Color c) {
        g.setColor(c);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        AffineTransform at = g.getTransform();
        g.rotate(-Math.PI / 4, x + 8, y + 8);
        g.draw(new RoundRectangle2D.Double(x - 1, y + 4.5, 10, 7, 7, 7));
        g.draw(new RoundRectangle2D.Double(x + 7, y + 4.5, 10, 7, 7, 7));
        g.setTransform(at);
        g.setStroke(old);
    }
    public static void doc(Graphics2D g, double x, double y, Color c) {
        poly(g, c, 1.8f, x + 9, y, x, y, x, y + 16, x + 13, y + 16, x + 13, y + 4, x + 9, y, x + 9, y + 4, x + 13, y + 4);
    }
    public static void share(Graphics2D g, double x, double y, Color c) {
        poly(g, c, 2f, x, y + 9, x, y + 18, x + 18, y + 18, x + 18, y + 9);
        line(g, x + 9, y, x + 9, y + 12, c, 2f);
        poly(g, c, 2f, x + 4, y + 5, x + 9, y, x + 14, y + 5);
    }
    public static void pulse(Graphics2D g, double x, double y, Color c) {
        poly(g, c, 2f, x, y + 8, x + 5, y + 8, x + 8, y + 2, x + 12, y + 15, x + 15, y + 8, x + 20, y + 8);
    }
    public static void monitor(Graphics2D g, double x, double y, Color c) {
        g.setColor(c);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Double(x, y, 18, 13, 4, 4));
        g.setStroke(old);
        line(g, x + 5, y + 17, x + 13, y + 17, c, 2f);
        play(g, x + 9, y + 6.5, 7, c);
    }
    public static void coin(Graphics2D g, double cx, double cy, Color c) {
        ring(g, cx, cy, 6.5, c, 1.8f);
        g.setFont(b(8));
        textBox(g, "P", cx - 6, cy - 6, 12, 12, b(8), c);
    }
}
