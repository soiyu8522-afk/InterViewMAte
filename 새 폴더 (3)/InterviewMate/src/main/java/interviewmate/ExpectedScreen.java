package interviewmate;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 12. 내 자소서 예상 질문 */
public class ExpectedScreen extends Page {
    private String filter = "전체";
    private final boolean[] open = new boolean[Data.EXPECTED.length];

    public ExpectedScreen() {
        super("expected", G.BG, 152, App.H - 86, 520);
        open[0] = true;
    }

    private List<Data.Expected> shown() {
        List<Data.Expected> l = new ArrayList<>();
        for (Data.Expected e : Data.EXPECTED) if (filter.equals("전체") || e.cat.equals(filter)) l.add(e);
        return l;
    }

    @Override protected void drawFixed(Graphics2D g) {
        App.header(g, this, "내 자소서 예상 질문", "면접관은 자소서의 이 문장들을 파고들 거예요", G.INK, G.SUB);
        String[] cats = {"전체", "경험 검증", "꼬리질문", "약점 공략"};
        double x = 40;
        for (String c : cats) {
            int n = 0;
            for (Data.Expected e : Data.EXPECTED) if (c.equals("전체") || e.cat.equals(c)) n++;
            String label = c + " " + n;
            int w = G.width(g, label, G.b(13)) + 30;
            SelectScreen.chipSmall(g, label, x, 104, w, filter.equals(c));
            hit(x, 104, w, 37, () -> { filter = c; scrollTop(); });
            x += w + 8;
        }
        G.line(g, 0, App.H - 86, App.CW, App.H - 86, G.TRACK, 1);
        G.text(g, "모의 면접에 쓸 질문", 40, App.H - 66, G.f(12.5f), G.SUB);
        G.text(g, shown().size() + "개 (" + filter + ")", 40, App.H - 46, G.b(15), G.INK);
        App.button(g, this, 760, App.H - 70, 260, 50, "이 질문들로 모의 면접 보기", G.BLUE, G.WHITE, () -> {
            List<String> q = new ArrayList<>();
            for (Data.Expected e : shown()) q.add(e.q);
            App.customQuestions = q;
            App.bossMode = false;
            App.go("select");
        });
    }

    @Override protected void drawBody(Graphics2D g, Body b) {
        double[] colY = {8, 8};
        double[] colX = {40, 540};
        double w = 480;
        for (Data.Expected e : shown()) {
            int col = colY[0] <= colY[1] ? 0 : 1;   // 더 짧은 열에 붙이기
            double x = colX[col], y = colY[col];
            int idx = java.util.Arrays.asList(Data.EXPECTED).indexOf(e);
            int qh = G.split(g, e.q, w - 44, G.b(16)).size() * 25;
            int gh = open[idx] ? G.split(g, e.guide, w - 72, G.f(13.5f)).size() * 21 + 48 : 0;
            double h = 20 + 28 + 12 + 60 + 16 + qh + 14 + (open[idx] ? gh + 12 : 0) + 40 + 20;
            G.rr(g, x, y, w, h, 18, G.WHITE);
            double cy = y + 20;
            Color[] c = catColor(e.cat);
            double tx = x + 22 + G.tag(g, e.cat, x + 22, cy, 28, G.b(12.5f), c[0], c[1], 10, 8) + 6;
            if (e.priority) G.tag(g, "우선 연습", tx, cy, 28, G.b(12.5f), G.INK, G.WHITE, 10, 8);
            cy += 40;
            G.rr(g, x + 22, cy, w - 44, 60, 11, new Color(0xF7F8FA));
            G.dashed(g, x + 22, cy, w - 44, 60, 11, G.LINE);
            G.text(g, e.from, x + 36, cy + 10, G.b(12), G.SLATE);
            G.text(g, e.quote, x + 36, cy + 32, G.f(13.5f), G.TEXT);
            cy += 76;
            G.wrap(g, e.q, x + 22, cy, w - 44, G.b(16), G.INK, 25);
            cy += qh + 14;
            if (open[idx]) {
                G.rr(g, x + 22, cy, w - 44, gh, 14, G.BLUE_SOFT);
                G.text(g, "답변 가이드", x + 36, cy + 15, G.b(13), new Color(0x1E3A8A));
                G.wrap(g, e.guide, x + 36, cy + 40, w - 72, G.f(13.5f), G.TEXT, 21);
                cy += gh + 12;
            }
            G.rr(g, x + 22, cy, 100, 40, 12, G.WHITE);
            G.rrLine(g, x + 22, cy, 100, 40, 12, G.LINE, 1.2f);
            G.textBox(g, open[idx] ? "가이드 접기" : "답변 가이드", x + 22, cy, 100, 40, G.b(13), G.INK);
            b.on(x + 22, cy, 100, 40, () -> open[idx] = !open[idx]);
            double pw = w - 44 - 110;
            G.rr(g, x + 132, cy, pw, 40, 12, G.BLUE_SOFT);
            String label = "이 질문 연습하기";
            int lw = G.width(g, label, G.b(13.5f));
            double lx = x + 132 + pw / 2 - (lw + 16) / 2.0;
            G.circle(g, lx + 4, cy + 20, 4, new Color(0x1E3A8A));
            G.text(g, label, lx + 16, cy + 10, G.b(13.5f), new Color(0x1E3A8A));
            b.on(x + 132, cy, pw, 40, () -> {
                App.customQuestions = new ArrayList<>(Collections.singletonList(e.q));
                App.bossMode = false;
                App.go("select");
            });
            colY[col] = y + h + 16;
        }
        b.height((int) Math.max(colY[0], colY[1]) + 14);
    }

    private Color[] catColor(String cat) {
        if (cat.equals("경험 검증")) return new Color[]{G.BLUE_SOFT, new Color(0x1E3A8A)};
        if (cat.equals("꼬리질문")) return new Color[]{G.GREEN_SOFT, G.GREEN};
        return new Color[]{G.ORANGE_SOFT, G.ORANGE};
    }
}
