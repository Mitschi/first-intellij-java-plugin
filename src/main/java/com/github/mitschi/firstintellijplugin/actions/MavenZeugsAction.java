package com.github.mitschi.firstintellijplugin.actions;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
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
//
//        System.out.println("TESTTEST");
////
        VirtualFile virtualFile = mavenProject.getFile();
//        File projectFile = MavenWslUtil.resolveWslAware(project,
//                () -> new File(virtualFile.getPath()),
//                wsl -> MavenWslUtil.getWslFile(wsl,new File(virtualFile.getPath())));
//
//        System.out.println(virtualFile);
//        System.out.println(projectFile);
//
        FileViewProvider fileViewProvider = PsiManager.getInstance(project).findViewProvider(virtualFile);
        PsiFile psiFile = fileViewProvider.getPsi(XMLLanguage.INSTANCE);
        XmlFile xmlFile = (XmlFile) psiFile;
//
//        System.out.println(xmlFile);
//
//        XmlTag[] dependencies = xmlFile.getRootTag().findSubTags("dependencies");
//        System.out.println(dependencies.length);
//
//        XmlTag dependenciesTag = Arrays.stream(dependencies).findFirst().get();
//
//        XmlTag[] subTags = dependenciesTag.findSubTags("dependency");
//        for (XmlTag dependencyTag : subTags) {
//            XmlTag groupId = Arrays.stream(dependencyTag.findSubTags("groupId")).findFirst().get();
//            XmlTag artifactId = Arrays.stream(dependencyTag.findSubTags("artifactId")).findFirst().get();
//            XmlTag version = Arrays.stream(dependencyTag.findSubTags("version")).findFirst().get();
//
//            System.out.println("DEP: "+groupId.getValue().getText()+":"+artifactId.getValue().getText()+":"+version.getValue().getText());
//
//            PsiElement newTag = version.copy();
////            newTag.
//
////            version.replace(newTag)
//        }

        DomFileElement<MavenDomProjectModel> domFileElement = DomManager.getDomManager(project).getFileElement(xmlFile, MavenDomProjectModel.class);

        final MavenDomProjectModel rootElement = (MavenDomProjectModel) domFileElement.getRootElement();
        final MavenDomDependencies mavenDomDependencies = rootElement.getDependencies();

        for (MavenDomDependency mavenDomDependency : mavenDomDependencies.getDependencies()) {
            System.out.println("ASDF");
            mavenDomDependency.undefine();

        }


//        manager.evaluateEffectivePom(mavenProject, s -> ApplicationManager.getApplication().invokeLater(() -> {
//            if (project.isDisposed()) return;
//
//            if (s == null) { // null means UnsupportedOperationException
//                new Notification(MavenUtil.MAVEN_NOTIFICATION_GROUP,
//                        MavenProjectBundle.message("maven.effective.pom.failed.title"),
//                        MavenProjectBundle.message("maven.effective.pom.failed"),
//                        NotificationType.ERROR).notify(project);
//                return;
//            }
//
//            String fileName = mavenProject.getMavenId().getArtifactId() + "-effective-pom.xml";
//            PsiFile file1 = PsiFileFactory.getInstance(project).createFileFromText(fileName, XMLLanguage.INSTANCE, s);
//            try {
//                file1.getVirtualFile().setWritable(false);
//            }
//            catch (IOException e) {
//                LOG.error(e);
//            }
//
//            file1.navigate(true);
//        }));


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
        final Project project = MavenActionUtil.getProject(event.getDataContext());
        if(project == null) return;
        final VirtualFile file = findPomXml(event.getDataContext());
        if (file == null) return;

        if (!MavenServerManager.getInstance().isUseMaven2()) {
            actionPerformed(project, file);
        }
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
