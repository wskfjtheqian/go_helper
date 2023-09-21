package com.golang.helper.go_helper;

import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WindowFactory implements ToolWindowFactory {
    EditorTextField editCode = new EditorTextField();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent( editCode, "", false);
        toolWindow.getContentManager().addContent(content);

    }

    public static void show(Project project, String text) {
        ToolWindow tool = ToolWindowManager.getInstance(project).getToolWindow("GoLangCode2");
        if (null != tool) {
            tool.show();
            @NotNull JComponent component = tool.getContentManager().getContent(0).getComponent();

            EditorTextField edit = (EditorTextField) component;
            edit.setSize(400,800);

            edit.setText(text);
            setting(edit.getEditor(false));
        }
    }


    private static void setting(EditorEx editor) {
        if (null != editor) {
            editor.setEmbeddedIntoDialogWrapper(true);
            final EditorSettings settings = editor.getSettings();
            settings.setLineNumbersShown(true);
            settings.setFoldingOutlineShown(true);
            settings.setRightMarginShown(true);
            settings.setLineMarkerAreaShown(true);
            settings.setIndentGuidesShown(true);
            editor.setHorizontalScrollbarVisible(true);
            editor.setVerticalScrollbarVisible(true);
            editor.setColorsScheme(EditorColorsManager.getInstance().getGlobalScheme());
            editor.setContextMenuGroupId("EditorPopupMenu");
        }
    }
}
