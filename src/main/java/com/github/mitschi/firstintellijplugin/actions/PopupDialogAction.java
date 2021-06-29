package com.github.mitschi.firstintellijplugin.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class PopupDialogAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        BrowserUtil.browse("http://www.orf.at");

//        Messages.showDialog("Test Message","TestTitle",arrayOf<String>() ,0,null)

        //https://github.com/JetBrains/intellij-community/blob/0e2aa4030ee763c9b0c828f0b5119f4cdcc66f35/plugins/maven/src/main/java/org/jetbrains/idea/maven/project/actions/MavenShowEffectivePom.java
        //https://github.com/JetBrains/intellij-community/blob/0e2aa4030ee763c9b0c828f0b5119f4cdcc66f35/plugins/maven/src/main/java/org/jetbrains/idea/maven/utils/actions/MavenActionUtil.java

    }

    @Override
    public boolean isDumbAware() {
        return super.isDumbAware();
    }
}
