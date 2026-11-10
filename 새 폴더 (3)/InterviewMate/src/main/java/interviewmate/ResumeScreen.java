package interviewmate;

import java.awt.*;
import java.util.List;

/** 11. 자기소개서 피드백 */
public class ResumeScreen extends Page {
    private int tab = 0;
    private final List<Data.Sentence>[] sections;
    private final int[][] scores = new int[3][];
    private final int[] selFix = {-1, -1, -1};
    private static final double TL = 62, TR = 578;   // 본문 글자 영역 왼쪽/오른쪽

    @SuppressWarnings("unchecked")
    public ResumeScreen() {
        super("resume", G.BG, 100, App.H - 86, 560);
        sections = new List[3];
        for (int i = 0; i < 3; i++) { sections[i] = Data.resumeSection(i); scores[i] = Data.RESUME_SCORES[i].clone(); }
    }

    private int currentFix() {
        List<Data.Sentence> s = sections[tab];
        if (selFix[tab] >= 0 && selFix[tab] < s.size() && s.get(selFix[tab]).kind == 1) return selFix[tab];
        int first = -1, count = 0;
        for (int i = 0; i < s.size(); i++) if (s.get(i).kind == 1) {
            if (first < 0) first = i;
            if (++count == 2 && tab == 0) return i;   // 목업처럼 '추상적인 성과'를 먼저 보여 줌
        }
        return first;
    }

    @Override protected void drawFixed(Graphics2D g) {
        App.title(g, "자기소개서 피드백", "[회사명] 백엔드 개발자 · 3문항", G.INK, G.SUB);
        G.rr(g, 966, 32, 54, 40, 12, G.WHITE);
        G.rrLine(g, 966, 32, 54, 40, 12, G.LINE, 1.2f);
        G.share(g, 984, 42, G.INK);
        hit(966, 32, 54, 40, () -> App.toast("공유 링크를 복사했어요"));
        G.line(g, 0, App.H - 86, App.CW, App.H - 86, G.TRACK, 1);
        int left = 0;
        for (List<Data.Sentence> sec : sections) for (Data.Sentence s : sec) if (s.kind == 1) left++;
        G.text(g, "남은 고칠 문장", 40, App.H - 66, G.f(12.5f), G.SUB);
        G.text(g, left + "개", 40, App.H - 46, G.b(15), left > 0 ? G.ORANGE : G.GREEN);
        App.arrowButton(g, this, 760, App.H - 70, 260, 50, "이 자소서로 예상 질문 뽑기", G.BLUE, () -> App.go("expected"));
    }

    @Override protected void drawBody(Graphics2D g, Body b) {
        // ===== 왼쪽: 문항 탭 + 본문 =====
        for (int i = 0; i < 3; i++) {
            double tx = 40 + i * 158;
            boolean on = tab == i;
            if (on) G.rr(g, tx, 8, 150, 42, 12, G.INK);
            else { G.rr(g, tx, 8, 150, 42, 12, G.WHITE); G.rrLine(g, tx, 8, 150, 42, 12, G.LINE, 1.2f); }
            G.textBox(g, Data.RESUME_TABS[i], tx, 8, 150, 42, G.b(14), on ? G.WHITE : G.TEXT);
            final int k = i;
            b.on(tx, 8, 150, 42, () -> tab = k);
        }
        List<Data.Sentence> ss = sections[tab];
        int chars = 0;
        for (Data.Sentence s : ss) chars += s.text.length();
        double top = 64;
        double textH = flow(g, b, ss, top + 54, false);
        double cardH = 54 + textH + 54;
        G.rr(g, 40, top, 560, cardH, 18, G.WHITE);
        G.text(g, Data.RESUME_TITLES[tab], TL, top + 20, G.b(15), G.INK);
        G.textR(g, Math.min(800, 400 + chars * 2) + " / 800자", TR, top + 21, G.f(13), G.SLATE);
        flow(g, b, ss, top + 54, true);
        double ly = top + cardH - 36;
        G.rr(g, TL, ly, 14, 14, 3, new Color(0xFED7AA));
        G.text(g, "고칠 문장", TL + 20, ly - 2, G.f(12.5f), G.SLATE);
        G.rr(g, TL + 100, ly, 14, 14, 3, new Color(0xC7D2FE));
        G.text(g, "살릴 문장", TL + 120, ly - 2, G.f(12.5f), G.SLATE);
        G.text(g, "주황색 문장을 누르면 오른쪽에 고칠 점이 나와요", TL + 210, ly - 2, G.f(12.5f), G.SUB);
        double leftBottom = top + cardH;

        // ===== 오른쪽: 점수 + 고칠 점 =====
        double rx = 620, rw = 400;
        String[] names = {"구체성", "직무 연관", "가독성", "차별성"};
        G.rr(g, rx, 8, rw, 92, 18, G.WHITE);
        for (int i = 0; i < 4; i++) {
            double cx = rx + 50 + i * 100;
            int v = scores[tab][i];
            G.textC(g, String.valueOf(v), cx, 24, G.b(21), v < 65 ? G.ORANGE : G.BLUE);
            G.textC(g, names[i], cx, 60, G.f(12.5f), G.SLATE);
        }
        double y = 116;
        int fi = currentFix();
        if (fi >= 0) {
            Data.Sentence s = ss.get(fi);
            double inner = rw - 44;
            int rh = G.split(g, s.fixReason, inner, G.f(13.5f)).size() * 21;
            int sh = G.split(g, s.suggestion, inner - 32, G.b(15)).size() * 24;
            double innerH = 34 + sh + 60;
            double h = 44 + rh + 16 + innerH + 20;
            G.rr(g, rx, y, rw, h, 18, G.CREAM);
            G.text(g, "고칠 점 · " + s.fixTitle, rx + 22, y + 18, G.b(13.5f), G.ORANGE);
            G.wrap(g, s.fixReason, rx + 22, y + 46, inner, G.f(13.5f), G.TEXT, 21);
            double iy = y + 46 + rh + 16;
            G.rr(g, rx + 22, iy, inner, innerH, 14, G.WHITE);
            G.text(g, "이렇게 바꿔 보세요", rx + 38, iy + 16, G.b(12), G.SLATE);
            G.wrap(g, s.suggestion, rx + 38, iy + 40, inner - 32, G.b(15), G.INK, 24);
            double by = iy + 40 + sh + 8;
            G.rr(g, rx + 38, by, 92, 36, 10, G.WHITE);
            G.rrLine(g, rx + 38, by, 92, 36, 10, G.LINE, 1.2f);
            G.textBox(g, "문장 바꾸기", rx + 38, by, 92, 36, G.b(13), G.INK);
            b.on(rx + 38, by, 92, 36, () -> {
                boolean lead = s.text.startsWith(" ");
                s.text = (lead ? " " : "") + s.suggestion;
                s.kind = 2;
                selFix[tab] = -1;
                scores[tab][0] = Math.min(95, scores[tab][0] + 9);
                scores[tab][1] = Math.min(95, scores[tab][1] + 5);
                scores[tab][3] = Math.min(95, scores[tab][3] + 8);
                App.toast("문장을 바꿨어요");
            });
            y += h;
        } else {
            G.rr(g, rx, y, rw, 76, 18, G.GREEN_SOFT);
            G.text(g, "이 문항의 고칠 문장을 모두 바꿨어요!", rx + 22, y + 18, G.b(15), G.GREEN);
            G.text(g, "다른 문항 탭도 확인해 보세요.", rx + 22, y + 44, G.f(13), G.TEXT);
            y += 76;
        }
        b.height((int) Math.max(leftBottom, y) + 30);
    }

    /** 문장 흐름 배치. paint=false 면 높이만 계산 */
    private double flow(Graphics2D g, Body b, List<Data.Sentence> ss, double top, boolean paint) {
        Font f = G.f(15);
        FontMetrics fm = g.getFontMetrics(f);
        double lh = 32, x = TL, y = top;
        for (int si = 0; si < ss.size(); si++) {
            Data.Sentence s = ss.get(si);
            String t = s.text;
            if (t.startsWith(" ")) { x += fm.charWidth(' ') + 4; t = t.substring(1); if (x >= TR) { x = TL; y += lh; } }
            StringBuilder run = new StringBuilder();
            double runX = x;
            for (int i = 0; i < t.length(); i++) {
                char ch = t.charAt(i);
                double cw = fm.charWidth(ch);
                if (x + cw > TR) {
                    if (paint) paintRun(g, b, run.toString(), runX, y, s, si, f);
                    run.setLength(0);
                    x = TL; y += lh; runX = x;
                    if (ch == ' ') continue;
                }
                run.append(ch);
                x += cw;
            }
            if (paint) paintRun(g, b, run.toString(), runX, y, s, si, f);
        }
        return y - top + lh;
    }

    private void paintRun(Graphics2D g, Body b, String run, double x, double y, Data.Sentence s, int si, Font f) {
        if (run.isEmpty()) return;
        double w = G.width(g, run, f);
        if (s.kind == 1) {
            boolean sel = si == currentFix();
            G.rr(g, x - 2, y - 3, w + 4, 27, 3, sel ? new Color(0xFDBA74) : new Color(0xFED7AA));
            G.rr(g, x - 2, y + 22, w + 4, 2, 1, G.ORANGE);
            b.on(x - 2, y - 3, w + 4, 27, () -> selFix[tab] = si);
        } else if (s.kind == 2) {
            G.rr(g, x - 2, y - 3, w + 4, 27, 3, new Color(0xC7D2FE));
            G.rr(g, x - 2, y + 22, w + 4, 2, 1, G.BLUE);
        }
        G.text(g, run, x, y, f, G.INK);
    }
}
