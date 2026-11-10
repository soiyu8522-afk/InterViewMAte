package interviewmate;

import javax.swing.*;
import java.awt.*;

/**
 * 노트북 화면의 뼈대: 왼쪽 메뉴(고정) + 오른쪽 본문.
 * 본문은 위아래 고정 영역(drawFixed)과 가운데 스크롤 영역(drawBody)으로 나뉩니다.
 * 두 곳 모두 좌표는 '본문 기준'(메뉴 오른쪽 끝이 x=0, 본문 너비 App.CW)입니다.
 */
public abstract class Page extends Canvas {
    protected final Body body;
    protected final JScrollPane sp;
    private final String name;

    /**
     * @param name   화면 이름 (메뉴에서 어떤 항목을 강조할지)
     * @param top    스크롤 영역 시작 y
     * @param bottom 스크롤 영역 끝 y
     * @param bodyH  스크롤 내용의 처음 높이 (drawBody 안에서 b.height()로 바꿀 수 있음)
     */
    protected Page(String name, Color bg, int top, int bottom, int bodyH) {
        super(App.W, App.H);
        this.name = name;
        setBackground(bg);
        body = new Body(bodyH, bg);
        sp = scroll(body, bg);
        place(sp, App.SIDE, top, App.CW, bottom - top);
        add(sp);
    }

    @Override protected final void draw(Graphics2D g) {
        App.sidebar(g, this, name);
        g.translate(App.SIDE, 0);
        ox = App.SIDE;
        drawFixed(g);
        ox = 0;
    }

    /** 고정 영역 (제목, 아래쪽 버튼 등). 없으면 비워 둠 */
    protected void drawFixed(Graphics2D g) {}

    protected abstract void drawBody(Graphics2D g, Body b);

    protected void scrollTop() { SwingUtilities.invokeLater(() -> sp.getVerticalScrollBar().setValue(0)); }

    public class Body extends Canvas {
        Body(int h, Color bg) { super(App.CW, h); setBackground(bg); }
        @Override protected void draw(Graphics2D g) { drawBody(g, this); }
        public void on(double x, double y, double w, double h, Runnable r) { hit(x, y, w, h, r); }
        public void height(int h) { setHeight(h); }
    }
}
