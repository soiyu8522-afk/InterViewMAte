package interviewmate;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 14. 면접 후기 공유하기 */
public class ReviewScreen extends Page {
    private final JTextField company = field("[회사명]");
    private final JTextField jobTime = field("백엔드 · [시기]");
    private final String[] stages = {"서류", "코딩 테스트", "1차 실무", "2차 임원", "인적성"};
    private final boolean[] stageOn = {true, true, true, false, false};
    private final List<String> questions = new ArrayList<>(Arrays.asList(
            "가장 큰 장애와 그때 내린 판단을 설명해 주세요.", "트랜잭션 격리 수준을 바꿔 본 경험이 있나요?"));
    private int result = 0;
    private boolean verify = true;
    private static final Color BROWN = new Color(0x92400E);

    public ReviewScreen() {
        super("review", G.BG, 100, App.H - 86, 560);
        place(company, 56, 44, 200, 30);
        place(jobTime, 304, 44, 200, 30);
        body.add(company);
        body.add(jobTime);
    }

    private static JTextField field(String text) {
        JTextField f = new JTextField(text);
        f.setBorder(null);
        f.setFont(G.f((float) (14.5 * App.S)));
        f.setForeground(G.TEXT);
        f.setBackground(G.WHITE);
        f.setCaretColor(G.BLUE);
        return f;
    }

    private int total() { return 50 + questions.size() * 10 + (verify ? 100 : 0); }

    @Override protected void drawFixed(Graphics2D g) {
        App.header(g, this, "면접 후기 공유하기", "다녀온 면접 정보를 나누면 포인트를 드려요", G.INK, G.SUB);
        G.line(g, 0, App.H - 86, App.CW, App.H - 86, G.TRACK, 1);
        G.text(g, "받을 포인트", 40, App.H - 66, G.f(12.5f), G.SUB);
        G.text(g, total() + "P", 40, App.H - 46, G.b(15), BROWN);
        App.button(g, this, 760, App.H - 70, 260, 50, "후기 올리고 " + total() + "P 받기", G.BLUE, G.WHITE, () -> {
            if (company.getText().trim().isEmpty() || company.getText().equals("[회사명]")) {
                App.toast("회사 이름을 입력해 주세요");
                company.requestFocus();
                return;
            }
            int p = total();
            App.points += p;
            App.toast(p + "P를 받았어요! 소중한 후기 고마워요");
            App.back();
        });
    }

    @Override protected void drawBody(Graphics2D g, Body b) {
        // ===== 왼쪽: 회사, 단계, 받은 질문 =====
        G.text(g, "회사", 40, 8, G.b(14), G.INK);
        G.text(g, "직무 · 시기", 288, 8, G.b(14), G.INK);
        box(g, 40, 34, 232, 48);
        box(g, 288, 34, 232, 48);
        b.on(40, 34, 232, 48, company::requestFocus);
        b.on(288, 34, 232, 48, jobTime::requestFocus);

        G.text(g, "거친 단계", 40, 100, G.b(14), G.INK);
        double x = 40, y = 126;
        for (int i = 0; i < stages.length; i++) {
            int w = G.width(g, stages[i], G.b(13)) + 34;
            if (x + w > 520) { x = 40; y += 44; }
            SelectScreen.chipSmall(g, stages[i], x, y, w, stageOn[i]);
            final int k = i;
            b.on(x, y, w, 37, () -> stageOn[k] = !stageOn[k]);
            x += w + 8;
        }
        y += 58;

        double h = 58 + questions.size() * 50 + 52;
        G.rr(g, 40, y, 480, h, 18, G.WHITE);
        G.text(g, "받은 질문", 62, y + 20, G.b(14.5f), G.INK);
        G.textR(g, "질문 1개당 +10P", 498, y + 21, G.b(12.5f), BROWN);
        double qy = y + 54;
        for (int i = 0; i < questions.size(); i++) {
            G.rr(g, 56, qy, 448, 42, 11, new Color(0xF7F8FA));
            G.text(g, "Q" + (i + 1), 70, qy + 12, G.b(13), G.SLATE);
            String q = questions.get(i);
            while (G.width(g, q, G.f(13.5f)) > 360) q = q.substring(0, q.length() - 2) + "…";
            G.text(g, q, 100, qy + 12, G.f(13.5f), G.INK);
            G.textR(g, "×", 490, qy + 10, G.f(16), G.SUB);
            final int k = i;
            b.on(466, qy, 38, 42, () -> questions.remove(k));
            qy += 50;
        }
        G.dashed(g, 56, qy, 448, 42, 11, new Color(0x9CA3AF));
        G.textBox(g, "+ 질문 추가", 56, qy, 448, 42, G.b(13.5f), G.TEXT);
        b.on(56, qy, 448, 42, () -> {
            String q = JOptionPane.showInputDialog(this, "받은 질문을 적어 주세요");
            if (q != null && !q.trim().isEmpty()) questions.add(q.trim());
        });
        double leftBottom = y + h;

        // ===== 오른쪽: 결과, 인증, 포인트 =====
        double rx = 540, rw = 480, ry = 8;
        G.text(g, "결과", rx, ry, G.b(14), G.INK);
        ry += 26;
        G.rr(g, rx, ry, rw, 48, 12, new Color(0xE5E7EB));
        String[] r = {"합격", "불합격", "결과 대기"};
        double cw = (rw - 8) / 3;
        for (int i = 0; i < 3; i++) {
            double tx = rx + 4 + i * cw;
            if (result == i) G.rr(g, tx, ry + 4, cw, 40, 9, G.WHITE);
            G.textBox(g, r[i], tx, ry + 4, cw, 40, G.b(14.5f), result == i ? G.INK : G.SLATE);
            final int k = i;
            b.on(tx, ry, cw, 48, () -> result = k);
        }
        ry += 66;

        G.rr(g, rx, ry, rw, 100, 18, G.WHITE);
        G.checkbox(g, rx + 22, ry + 22, 22, verify);
        G.text(g, "면접 참여 인증하기 · +100P", rx + 58, ry + 20, G.b(15), G.INK);
        G.wrap(g, "면접 안내 메일이나 문자 캡처를 올려 주세요. 이름·연락처는 자동으로 가려지고 인증 후 바로 삭제돼요.",
                rx + 58, ry + 46, rw - 80, G.f(13), G.SUB, 20);
        b.on(rx, ry, rw, 100, () -> verify = !verify);
        ry += 116;

        G.rr(g, rx, ry, rw, 150, 18, G.YELLOW_SOFT);
        G.text(g, "받을 포인트", rx + 22, ry + 20, G.b(14), BROWN);
        String[][] rows = {{"기본 후기", "+50P"}, {"질문 " + questions.size() + "개", "+" + questions.size() * 10 + "P"},
                {"참여 인증", verify ? "+100P" : "+0P"}};
        for (int i = 0; i < 3; i++) {
            G.text(g, rows[i][0], rx + 22, ry + 54 + i * 28, G.f(14), G.TEXT);
            G.textR(g, rows[i][1], rx + rw - 22, ry + 54 + i * 28, G.b(14), BROWN);
        }
        ry += 150;
        b.height((int) Math.max(leftBottom, ry) + 30);
    }

    private void box(Graphics2D g, double x, double y, double w, double h) {
        G.rr(g, x, y, w, h, 12, G.WHITE);
        G.rrLine(g, x, y, w, h, 12, G.LINE, 1.2f);
    }
}
