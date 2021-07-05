package com.github.mitschi.firstintellijplugin.actions;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.dom.model.MavenDomDependencies;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomProjectModel;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectBundle;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.server.MavenServerManager;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.utils.MavenWslUtil;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class MavenZeugsAction extends AnAction implements DumbAware {

    private static final Logger LOG = Logger.getInstance(MavenZeugsAction.class);

    public static void actionPerformed(@NotNull final Project project, @NotNull final VirtualFile file) {
        final MavenProjectsManager manager = MavenProjectsManager.getInstance(project);

        final MavenProject mavenProject = manager.findProject(file);
        assert mavenProject != null;

        VirtualFile virtualFile = mavenProject.getFile();

        FileViewProvider fileViewProvider = PsiManager.getInstance(project).findViewProvider(virtualFile);
        PsiFile psiFile = fileViewProvider.getPsi(XMLLanguage.INSTANCE);
        XmlFile xmlFile = (XmlFile) psiFile;
        DomFileElement<MavenDomProjectModel> domFileElement = DomManager.getDomManager(project).getFileElement(xmlFile, MavenDomProjectModel.class);

        final MavenDomProjectModel rootElement = (MavenDomProjectModel) domFileElement.getRootElement();
        final MavenDomDependencies mavenDomDependencies = rootElement.getDependencies();

        for (MavenDomDependency mavenDomDependency : mavenDomDependencies.getDependencies()) {
            System.out.println("ASDF");
            System.out.println(mavenDomDependency);
//            mavenDomDependency.getXmlTag().delete();
//            mavenDomDependency.undefine();
            XmlTag versionTag = Arrays.stream(mavenDomDependency.getXmlTag().findSubTags("version")).findFirst().get();
            String oldVersion = versionTag.getValue().getText();
//            Arrays.stream(mavenDomDependency.getXmlTag().findSubTags("version")).findFirst().get().delete();
            System.out.println(versionTag);
            versionTag.getValue().setText("3.7.2");
            String newVersion = versionTag.getValue().getText();
            System.out.println(versionTag);

            final Notification notification = new Notification("buildtools.notifications", "",
                    "Changed version from "+oldVersion+" to " +newVersion , NotificationType.INFORMATION);
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    Notifications.Bus.notify(notification, project);
                }
            });
        }

    }

    @Nullable
    private static VirtualFile findPomXml(@NotNull DataContext dataContext) {
        VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(dataContext);
        if (file == null) return null;

        if (file.isDirectory()) {
            file = MavenUtil.streamPomFiles(MavenActionUtil.getProject(dataContext), file).findFirst().orElse(null);
            if (file == null) return null;
        }

        MavenProjectsManager manager = MavenActionUtil.getProjectsManager(dataContext);
        if(manager == null) return null;
        MavenProject mavenProject = manager.findProject(file);
        if (mavenProject == null) return null;

        return file;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // CommandProcessor for undo and formatting
        final Project project = MavenActionUtil.getProject(event.getDataContext());
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        final Project project = MavenActionUtil.getProject(event.getDataContext());
                        if(project == null) return;
                        final VirtualFile file = findPomXml(event.getDataContext());
                        if (file == null) return;

                        if (!MavenServerManager.getInstance().isUseMaven2()) {
                            actionPerformed(project, file);
                        }
                    }
                });
            }
        }, "Remove", "MavenRunHelper");

    }

//    @Override
//    public void update(@NotNull AnActionEvent e) {
//        Presentation p = e.getPresentation();
//
//        boolean visible = findPomXml(e.getDataContext()) != null;
//
//        visible = visible && !MavenServerManager.getInstance().isUseMaven2();
//        p.setVisible(visible);
//    }

}
