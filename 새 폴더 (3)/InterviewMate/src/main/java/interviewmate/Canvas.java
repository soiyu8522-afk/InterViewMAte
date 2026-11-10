package interviewmate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * 직접 그리는 패널. draw() 안에서 hit(x,y,w,h, 동작)으로 클릭 영역을 등록하면
 * 그 영역을 눌렀을 때 동작이 실행됩니다.
 */
public abstract class Canvas extends JPanel {
    private static class Hit {
        final Rectangle2D r; final Runnable action;
        Hit(Rectangle2D r, Runnable a) { this.r = r; this.action = a; }
    }
    private final List<Hit> hits = new ArrayList<>();

    protected Canvas(int w, int h) {
        setLayout(null);
        setPreferredSize(new Dimension(App.px(w), App.px(h)));
        setSize(App.px(w), App.px(h));
        setOpaque(true);
        MouseAdapter m = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                List<Hit> copy = new ArrayList<>(hits);
                for (int i = copy.size() - 1; i >= 0; i--) {   // 나중에 그린 것이 위에 있음
                    if (copy.get(i).r.contains(logical(e))) { copy.get(i).action.run(); repaintAll(); return; }
                }
            }
            @Override public void mouseMoved(MouseEvent e) {
                boolean over = false;
                for (Hit h : hits) if (h.r.contains(logical(e))) { over = true; break; }
                setCursor(over ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            }
        };
        addMouseListener(m);
        addMouseMotionListener(m);
    }

    /** 화면 배율을 뺀 원래(목업) 좌표 */
    private static java.awt.geom.Point2D logical(MouseEvent e) {
        return new java.awt.geom.Point2D.Double(e.getX() / App.S, e.getY() / App.S);
    }

    /** 자식 컴포넌트(스크롤, 입력칸)를 목업 좌표로 배치 */
    public static void place(Component c, double x, double y, double w, double h) {
        c.setBounds(App.px(x), App.px(y), App.px(w), App.px(h));
    }

    /** 클릭 후 창 전체 다시 그리기 (스크롤 영역의 변화가 고정 영역에도 반영되도록) */
    protected void repaintAll() {
        Container top = getTopLevelAncestor();
        if (top != null) top.repaint(); else repaint();
    }

    /** 본문을 오른쪽으로 옮겨 그릴 때(메뉴 옆) 클릭 영역도 같이 옮기기 위한 값 */
    protected double ox = 0;

    protected void hit(double x, double y, double w, double h, Runnable action) {
        hits.add(new Hit(new Rectangle2D.Double(x + ox, y, w, h), action));
    }

    /** 화면 높이를 바꿀 때 (스크롤 영역 안의 내용이 길어질 때) */
    protected void setHeight(int h) {
        h = App.px(h);
        if (getPreferredSize().height != h) {
            setPreferredSize(new Dimension(getPreferredSize().width, h));
            revalidate();
        }
    }

    @Override protected final void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        hits.clear();
        Graphics2D g = (Graphics2D) g0.create();
        G.smooth(g);
        g.scale(App.S, App.S);      // 목업 좌표로 그리고, 창 크기에 맞게 확대/축소
        draw(g);
        g.dispose();
    }

    protected abstract void draw(Graphics2D g);

    /** 이 화면이 다시 보일 때 호출 (상태 새로고침) */
    public void onShow() {}

    /** 스크롤되는 영역 만들기 */
    public static JScrollPane scroll(Canvas content, Color bg) {
        JScrollPane sp = new JScrollPane(content, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.getViewport().setBackground(bg);
        sp.getVerticalScrollBar().setUnitIncrement(App.px(18));
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        sp.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = new Color(0, 0, 0, 60);
                trackColor = new Color(0, 0, 0, 0);
            }
            @Override protected JButton createDecreaseButton(int o) { return zero(); }
            @Override protected JButton createIncreaseButton(int o) { return zero(); }
            private JButton zero() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b; }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g; G.smooth(g2);
                G.rr(g2, r.x, r.y, r.width, r.height, 3, thumbColor);
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {}
        });
        sp.setOpaque(false);
        return sp;
    }
}
