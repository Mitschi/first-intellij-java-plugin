package com.github.mitschi.firstintellijplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;


public class PopupDialogAction extends AnAction implements DumbAware {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("ASDFASDFASDFASDF");
    }

    @Override
    public boolean isDumbAware() {
        return super.isDumbAware();
    }
}
