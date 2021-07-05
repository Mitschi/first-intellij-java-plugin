package com.github.mitschi.firstintellijplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;


public class PopupDialogAction extends AnAction implements DumbAware {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("ASDFASDFASDFASDF");
        Project project = e.getProject();
        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                // handle the events
                //System.out.println(events);
                List<? extends VFileEvent> pomAndJavaEvents = events.stream().filter(x -> x.getFile().getName().endsWith(".java") || x.getFile().getName().equals("pom.xml")).collect(Collectors.toList());
                for (VFileEvent pomAndJavaEvent : pomAndJavaEvents) {
                    System.out.println(pomAndJavaEvent.getFile());
                }
                System.out.println("===========================================");

            }
        });
    }

    @Override
    public boolean isDumbAware() {
        return super.isDumbAware();
    }
}
