package gui.mainapp;

import engine.calculation.functions.MathFunctionType;
import engine.expressions.parser.ExpressionParser;
import engine.expressions.parser.auto_complete.AutocompletionParser;
import engine.expressions.parser.auto_complete.Completion;
import engine.expressions.parser.auto_complete.CompletionVisitor;
import gui.mainapp.viewport.EqualViewport;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/27/13
 * Time: 7:13 PM
 */
public class AutoCompleter {
    private final JTextArea equationPad;
    private final ExpressionParser parser;
    private JPopupMenu popup;
    private JList<CompletionVariant> list;
    private KeyAdapter listener;
    private int editPosition;
    private CompleteDocumentListener documentListener;

    public AutoCompleter(JTextArea equationPad, ExpressionParser parser) {
        this.equationPad = equationPad;
        this.parser = parser;

        listener = new CompleteTextAreaKeyListener();
        documentListener = new CompleteDocumentListener();
    }

    public void run() {
        int position = equationPad.getCaretPosition();

        String text = equationPad.getText().substring(0, position);

        AutocompletionParser autocompletionParser = parser.createAutocompletionParser();
        List<Completion> completions = autocompletionParser.completeExpression(
                EqualViewport.TOP_CLAUSE,
                text);

        AutocompletionVisitor visitor = new AutocompletionVisitor();
        for (Completion completion : completions) {
            completion.accept(visitor);
        }

        List<CompletionVariant> variants = visitor.variants;
        if (variants.isEmpty()) {
            hidePopup();
            return;
        }

        CompletionVariant[] variantsArr = variants.toArray(new CompletionVariant[visitor.variants.size()]);

        hidePopup();
        if (variantsArr.length == 1) {
            variantsArr[0].complete(position);
        } else {
            showPopup(variantsArr, position);
            equationPad.requestFocusInWindow();
        }
    }

    private void showPopup(CompletionVariant[] variants, int position) {
        Point location;
        try {
            location = equationPad.modelToView(position).getLocation();
        } catch (BadLocationException e2) {
            return;
        }

        editPosition = position;
        popup = createSuggestionPopup(location, variants);
        popup.setVisible(true);

        equationPad.addKeyListener(listener);
        equationPad
                .getDocument()
                .addDocumentListener(documentListener);
    }

    private void pressComplete() {
        int idx = list.getSelectedIndex();

        list.getModel()
                .getElementAt(idx).complete(editPosition);

        hidePopup();
    }

    private void pressUp() {
        int idx = list.getSelectedIndex();
        if (idx > 0) {
            list.setSelectedIndex(idx - 1);
            list.ensureIndexIsVisible(list.getSelectedIndex());
        }
    }

    private void pressDown() {
        int idx = list.getSelectedIndex();
        if (idx < list.getModel().getSize() - 1) {
            list.setSelectedIndex(idx + 1);
            list.ensureIndexIsVisible(list.getSelectedIndex());
        }
    }

    private void hidePopup() {
        if (popup != null) {
            equationPad.removeKeyListener(listener);
            equationPad
                    .getDocument()
                    .removeDocumentListener(documentListener);
            editPosition = -1;
            popup.setVisible(false);
            popup = null;
        }
    }

    public JPopupMenu createSuggestionPopup(Point location,
                                            CompletionVariant[] variants) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.removeAll();
        popupMenu.setOpaque(false);
        popupMenu.setBorder(null);
        JList<CompletionVariant> list = createSuggestionList(variants);
        JScrollPane pane = new JScrollPane(list);
        popupMenu.add(pane, BorderLayout.CENTER);
        popupMenu.show(equationPad,
                location.x,
                equationPad.getBaseline(0, 0)
                        + location.y);

        return popupMenu;
    }

    private JList<CompletionVariant> createSuggestionList(CompletionVariant[] variants) {
        list = new JList<CompletionVariant>(variants);
        list.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {

                }
            }
        });
        return list;
    }

    private class CompletionVariant {
        private final String text;
        private final String appendText;

        private CompletionVariant(String text, String appendText) {
            this.text = text;
            this.appendText = appendText;
        }

        public String getText() {
            return text;
        }

        public String getAppendText() {
            return appendText;
        }

        @Override
        public String toString() {
            return text;
        }

        public void complete(int editPosition) {
            equationPad.insert(appendText, editPosition);
        }
    }

    private class AutocompletionVisitor implements CompletionVisitor {
        private List<CompletionVariant> variants = new ArrayList<CompletionVariant>();

        @Override
        public void functionName(String prefix) {
            for (MathFunctionType type : MathFunctionType.values()) {
                if (type.getSignature().startsWith(prefix)) {
                    String appendText = type.getSignature().substring(prefix.length());
                    variants.add(new CompletionVariant(type.getSignature(), appendText));
                }
            }
        }

        @Override
        public void variableName(String prefix) {
            addVars(prefix, parser.getVarList());
            addVars(prefix, parser.getKnownConstants().keySet());
        }

        private void addVars(String prefix, Collection<String> list) {
            for (String var : list) {
                if (var.startsWith(prefix)) {
                    String appendText = var.substring(prefix.length());
                    variants.add(new CompletionVariant(var, appendText));
                }
            }
        }

        @Override
        public void equalitySign(String prefix) {
        }

        @Override
        public void factorOperator() {
        }

        @Override
        public void termOperator() {
        }

        @Override
        public void closeBracket() {
        }

        @Override
        public void openBracket() {
        }

        @Override
        public void number(String prefix) {
        }
    }

    private class CompleteTextAreaKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    e.consume();
                    pressUp();
                    break;
                case KeyEvent.VK_DOWN:
                    e.consume();
                    pressDown();
                    break;
                case KeyEvent.VK_ENTER:
                    e.consume();
                    pressComplete();
                    break;
                case KeyEvent.VK_ESCAPE:
                    e.consume();
                    hidePopup();
                    break;
            }
        }
    }

    private class CompleteDocumentListener implements DocumentListener, Runnable {
        @Override
        public void insertUpdate(DocumentEvent e) {
            SwingUtilities.invokeLater(this);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            SwingUtilities.invokeLater(this);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            SwingUtilities.invokeLater(this);
        }

        @Override
        public void run() {
            AutoCompleter.this.run();
        }
    }
}
