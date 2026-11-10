package interviewmate;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/** 10. 채용공고 맞춤 면접 */
public class PostingScreen extends Page {
    private final JTextArea area = new JTextArea(App.postingText);
    private final Set<String> unchecked = new HashSet<>();
    private List<String[]> found = new ArrayList<>();

    private static final String SAMPLE_LINK = "[회사명] 서버 개발자 (신입)\n· Java/Spring 기반 API 개발\n· 데이터베이스 설계와 쿼리 튜닝\n· AWS 클라우드 운영, 원활한 협업";

    public PostingScreen() {
        super("posting", G.BG, 100, App.H - 86, 560);
        area.setFont(G.f((float) (14 * App.S)));
        area.setForeground(G.TEXT);
        area.setLineWrap(true);
        area.setBorder(null);
        area.setBackground(G.WHITE);
        area.setCaretColor(G.BLUE);
        JScrollPane asp = new JScrollPane(area);
        asp.setBorder(null);
        place(asp, 60, 52, 440, 156);
        body.add(asp);
        area.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { changed(); }
            public void removeUpdate(DocumentEvent e) { changed(); }
            public void changedUpdate(DocumentEvent e) { changed(); }
        });
    }

    private void changed() { App.postingText = area.getText(); repaintAll(); }

    /** 공고에서 키워드 찾기 (실제 서비스에선 AI가 할 일을 간단한 단어 매칭으로 흉내) */
    private List<String[]> analyze() {
        List<String[]> out = new ArrayList<>();
        for (String[] k : Data.KEYWORDS)
            if (Pattern.compile(k[1], Pattern.CASE_INSENSITIVE).matcher(App.postingText).find()) out.add(k);
        return out;
    }
    private boolean isGap(String[] k) {
        return !Pattern.compile(k[1], Pattern.CASE_INSENSITIVE).matcher(App.resumeText).find();
    }
    private List<String> selected() {
        List<String> q = new ArrayList<>();
        for (String[] k : found) if (!unchecked.contains(k[0])) q.add(k[2]);
        return q;
    }

    @Override protected void drawFixed(Graphics2D g) {
        found = analyze();
        App.title(g, "채용공고 맞춤 면접", "지원할 공고를 붙여넣으면 핵심 역량과 예상 질문을 뽑아 드려요", G.INK, G.SUB);
        G.line(g, 0, App.H - 86, App.CW, App.H - 86, G.TRACK, 1);
        List<String> q = selected();
        boolean ok = !q.isEmpty();
        G.text(g, "선택한 질문", 40, App.H - 66, G.f(12.5f), G.SUB);
        G.text(g, q.size() + "개 · 찾은 역량 " + found.size() + "개", 40, App.H - 46, G.b(15), G.INK);
        App.button(g, this, 760, App.H - 70, 260, 50, ok ? "이 질문으로 면접관 고르기" : "질문을 골라 주세요",
                ok ? G.BLUE : new Color(0x9CA3AF), G.WHITE, () -> {
                    if (!ok) return;
                    App.customQuestions = q;
                    App.bossMode = false;
                    App.go("select");
                });
    }

    @Override protected void drawBody(Graphics2D g, Body b) {
        found = analyze();
        // ===== 왼쪽: 공고 입력 + 핵심 역량 =====
        G.text(g, "지원할 공고", 40, 8, G.b(15), G.INK);
        G.rr(g, 40, 36, 480, 188, 18, G.WHITE);
        G.rrLine(g, 40, 36, 480, 188, 18, G.LINE, 1.2f);

        G.rr(g, 40, 236, 150, 40, 13, G.WHITE);
        G.rrLine(g, 40, 236, 150, 40, 13, G.LINE, 1.2f);
        G.link(g, 54, 248, G.TEXT);
        G.text(g, "링크로 불러오기", 78, 246, G.b(13), G.TEXT);
        b.on(40, 236, 150, 40, () -> {
            String url = JOptionPane.showInputDialog(this, "채용공고 링크를 붙여넣어 주세요", "https://");
            if (url != null && !url.trim().isEmpty()) {
                area.setText(SAMPLE_LINK);
                App.toast("예시 공고를 불러왔어요");
            }
        });
        G.rr(g, 200, 236, 140, 40, 13, G.WHITE);
        G.rrLine(g, 200, 236, 140, 40, 13, G.LINE, 1.2f);
        G.doc(g, 216, 248, G.TEXT);
        G.text(g, "내 자소서 연결", 238, 246, G.b(13), G.TEXT);
        b.on(200, 236, 140, 40, () -> App.go("resume"));

        double y = 292;
        List<String[]> gaps = new ArrayList<>();
        for (String[] k : found) if (isGap(k)) gaps.add(k);
        double chipsH = layoutChips(g, null, 0);
        double cardH = 58 + chipsH + (gaps.isEmpty() ? 4 : 16 + gapH(g, gaps)) + 20;
        G.rr(g, 40, y, 480, cardH, 18, G.WHITE);
        G.text(g, "공고에서 찾은 핵심 역량", 62, y + 20, G.b(15), G.INK);
        if (found.isEmpty()) G.text(g, "공고에서 아직 역량을 찾지 못했어요", 62, y + 56, G.f(13.5f), G.SUB);
        double cy = y + 54;
        layoutChips(g, g, cy);
        cy += chipsH + 16;
        if (!gaps.isEmpty()) {
            double gh = gapH(g, gaps);
            G.rr(g, 62, cy, 436, gh, 12, G.CREAM);
            G.text(g, "빈틈", 76, cy + 13, G.b(13), G.ORANGE);
            G.wrap(g, gapText(gaps), 116, cy + 13, 366, G.f(13.5f), G.TEXT, 21);
        }
        double leftBottom = y + cardH;

        // ===== 오른쪽: 예상 질문 =====
        double rx = 540, rw = 480;
        double listH = 0;
        for (String[] k : found) listH += G.split(g, k[2], rw - 90, G.b(15)).size() * 23 + 44;
        double qTop = 8;
        G.rr(g, rx, qTop, rw, Math.max(120, listH + 58), 18, G.WHITE);
        G.text(g, "예상 질문", rx + 22, qTop + 20, G.b(15), G.INK);
        G.textR(g, selected().size() + "개 선택됨", rx + rw - 22, qTop + 21, G.f(13), G.SLATE);
        double qy = qTop + 62;
        for (int i = 0; i < found.size(); i++) {
            String[] k = found.get(i);
            boolean on = !unchecked.contains(k[0]);
            G.checkbox(g, rx + 22, qy + 1, 21, on);
            int h = G.wrap(g, k[2], rx + 56, qy - 1, rw - 90, G.b(15), G.INK, 23);
            G.text(g, "근거: " + k[3], rx + 56, qy + h + 4, G.f(12.5f), G.SUB);
            b.on(rx, qy - 10, rw, h + 40, () -> { if (on) unchecked.add(k[0]); else unchecked.remove(k[0]); });
            qy += h + 44;
            if (i < found.size() - 1) G.line(g, rx + 22, qy - 16, rx + rw - 22, qy - 16, G.TRACK, 1);
        }
        b.height((int) Math.max(leftBottom, qTop + Math.max(120, listH + 58)) + 30);
    }

    /** 역량 칩 배치. draw 가 null 이면 높이만 계산 */
    private double layoutChips(Graphics2D measure, Graphics2D draw, double top) {
        double x = 62, y = top;
        for (String[] k : found) {
            int w = G.width(measure, k[0], G.b(13)) + 28;
            if (x + w > 500) { x = 62; y += 36; }
            if (draw != null) {
                boolean gap = isGap(k);
                G.rr(draw, x, y, w, 28, 14, gap ? G.ORANGE_SOFT : G.BLUE_SOFT);
                G.textBox(draw, k[0], x, y, w, 28, G.b(13), gap ? G.ORANGE : new Color(0x1E3A8A));
            }
            x += w + 8;
        }
        return found.isEmpty() ? 28 : y - top + 28;
    }
    private String gapText(List<String[]> gaps) {
        StringBuilder sb = new StringBuilder("자소서에 ");
        for (int i = 0; i < gaps.size(); i++) sb.append(i > 0 ? ", " : "").append("‘").append(gaps.get(i)[0]).append("’");
        return sb + " 경험이 없어요. 관련 질문이 나오면 비슷한 경험으로 연결하는 연습을 넣었어요.";
    }
    private double gapH(Graphics2D g, List<String[]> gaps) {
        return G.split(g, gapText(gaps), 366, G.f(13.5f)).size() * 21 + 26;
    }
}
