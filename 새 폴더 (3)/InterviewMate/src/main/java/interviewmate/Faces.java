package interviewmate;

import java.awt.*;
import java.awt.geom.*;

/** 면접관 얼굴 일러스트 (원·곡선만으로 그림) */
public final class Faces {
    private Faces() {}

    public enum Expr { SMILE, FLAT, ANGRY, FROWN, GRIN }

    public static final Color SKIN = new Color(0xF0C69C);
    public static final Color HAIR = new Color(0x2A1E17);
    public static final Color GRAY_HAIR = new Color(0x6B7280);

    /**
     * 얼굴 + 상반신. (cx, cy) = 얼굴 중심, r = 얼굴 반지름.
     * bodyBottom: 몸통이 끝나는 y (그 아래는 잘림)
     */
    public static void person(Graphics2D g, double cx, double cy, double r, Color body, Color hair,
                              Expr e, boolean glasses, boolean tie, double bodyBottom) {
        Shape oldClip = g.getClip();
        // 몸통
        double bw = r * 3.4;
        Path2D torso = new Path2D.Double();
        torso.moveTo(cx - bw / 2, bodyBottom);
        torso.curveTo(cx - bw / 2, cy + r * 1.35, cx - bw * 0.25, cy + r * 1.25, cx, cy + r * 1.25);
        torso.curveTo(cx + bw * 0.25, cy + r * 1.25, cx + bw / 2, cy + r * 1.35, cx + bw / 2, bodyBottom);
        torso.closePath();
        g.setColor(body);
        g.fill(torso);
        // 목
        G.rr(g, cx - r * 0.3, cy + r * 0.6, r * 0.6, r * 0.75, 0, SKIN.darker());
        if (tie) {
            Path2D collar = new Path2D.Double();
            collar.moveTo(cx - r * 0.45, cy + r * 1.27);
            collar.lineTo(cx + r * 0.45, cy + r * 1.27);
            collar.lineTo(cx, cy + r * 1.75);
            collar.closePath();
            g.setColor(Color.WHITE);
            g.fill(collar);
            Path2D t = new Path2D.Double();
            t.moveTo(cx - r * 0.12, cy + r * 1.35);
            t.lineTo(cx + r * 0.12, cy + r * 1.35);
            t.lineTo(cx + r * 0.2, cy + r * 2.3);
            t.lineTo(cx, cy + r * 2.5);
            t.lineTo(cx - r * 0.2, cy + r * 2.3);
            t.closePath();
            g.setColor(new Color(0x991B1B));
            g.fill(t);
        }
        // 머리카락 (뒤)
        g.setColor(hair);
        g.fill(new Ellipse2D.Double(cx - r * 1.02, cy - r * 1.08, r * 2.04, r * 1.7));
        // 얼굴
        g.setColor(SKIN);
        g.fill(new Ellipse2D.Double(cx - r, cy - r * 0.78, r * 2, r * 1.86));
        // 앞머리
        g.setColor(hair);
        Path2D fringe = new Path2D.Double();
        fringe.moveTo(cx - r * 1.0, cy - r * 0.2);
        fringe.curveTo(cx - r * 0.95, cy - r * 1.05, cx + r * 0.95, cy - r * 1.05, cx + r * 1.0, cy - r * 0.2);
        fringe.curveTo(cx + r * 0.6, cy - r * 0.62, cx - r * 0.6, cy - r * 0.62, cx - r * 1.0, cy - r * 0.2);
        g.fill(fringe);

        Color ink = new Color(0x1F2937);
        double ey = cy + r * 0.05, ex = r * 0.36, er = Math.max(1.6, r * 0.09);
        G.circle(g, cx - ex, ey, er, ink);
        G.circle(g, cx + ex, ey, er, ink);
        float sw = (float) Math.max(1.4, r * 0.08);
        if (e == Expr.ANGRY) {
            G.line(g, cx - ex - r * 0.25, ey - r * 0.33, cx - ex + r * 0.2, ey - r * 0.2, ink, sw * 1.3f);
            G.line(g, cx + ex + r * 0.25, ey - r * 0.33, cx + ex - r * 0.2, ey - r * 0.2, ink, sw * 1.3f);
        }
        if (e == Expr.FROWN) {
            G.line(g, cx - ex - r * 0.25, ey - r * 0.22, cx - ex + r * 0.22, ey - r * 0.32, ink, sw * 1.3f);
            G.line(g, cx + ex + r * 0.25, ey - r * 0.22, cx + ex - r * 0.22, ey - r * 0.32, ink, sw * 1.3f);
        }
        if (glasses) {
            g.setColor(ink);
            Stroke o = g.getStroke();
            g.setStroke(new BasicStroke(sw));
            g.draw(new RoundRectangle2D.Double(cx - ex - r * 0.27, ey - r * 0.2, r * 0.54, r * 0.4, r * 0.15, r * 0.15));
            g.draw(new RoundRectangle2D.Double(cx + ex - r * 0.27, ey - r * 0.2, r * 0.54, r * 0.4, r * 0.15, r * 0.15));
            g.draw(new Line2D.Double(cx - ex + r * 0.27, ey, cx + ex - r * 0.27, ey));
            g.setStroke(o);
        }
        Color mouth = new Color(0x9A3412);
        double my = cy + r * 0.48;
        switch (e) {
            case SMILE: arc(g, cx, my - r * 0.12, r * 0.28, r * 0.18, 200, 140, mouth, sw); break;
            case GRIN: arc(g, cx, my - r * 0.18, r * 0.36, r * 0.26, 195, 150, mouth, sw * 1.3f); break;
            case FROWN: arc(g, cx, my + r * 0.08, r * 0.3, r * 0.15, 20, 140, mouth, sw * 1.2f); break;
            default: G.line(g, cx - r * 0.25, my, cx + r * 0.25, my, mouth, sw * 1.3f);
        }
        g.setClip(oldClip);
    }

    private static void arc(Graphics2D g, double cx, double cy, double rx, double ry, double start, double ext, Color c, float sw) {
        g.setColor(c);
        Stroke o = g.getStroke();
        g.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Arc2D.Double(cx - rx, cy - ry, rx * 2, ry * 2, start, ext, Arc2D.OPEN));
        g.setStroke(o);
    }

    /** 동그란 배경 안의 아바타 (면접관 선택 카드용) */
    public static void avatar(Graphics2D g, double cx, double cy, double R, Data.Type t) {
        Shape old = g.getClip();
        Ellipse2D circle = new Ellipse2D.Double(cx - R, cy - R, R * 2, R * 2);
        g.setColor(t.circle);
        g.fill(circle);
        g.clip(circle);
        Expr e = t.expr;
        person(g, cx, cy - R * 0.12, R * 0.4, t.body, t.hair, e == Expr.ANGRY ? Expr.FLAT : e, t.glasses, false, cy + R);
        g.setClip(old);
    }

    /** 사람 실루엣 (내 카메라 자리) */
    public static void silhouette(Graphics2D g, double cx, double cy, double r, Color c) {
        G.circle(g, cx, cy, r, c);
        g.setColor(c);
        g.fill(new Ellipse2D.Double(cx - r * 1.75, cy + r * 1.25, r * 3.5, r * 3.2));
    }
}
