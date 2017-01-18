package fr.ironcraft.kubithon.launcher;

import fr.theshark34.openlauncherlib.util.CrashReporter;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.abstractcomponents.AbstractProgressBar;
import fr.theshark34.swinger.colored.SColoredBar;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public class LauncherPanel extends JPanel
{
    private Image background = Swinger.getResource("logo.png");
    private SColoredBar bar = new SColoredBar(Swinger.getTransparentWhite(75), Swinger.getTransparentWhite(125));
    private Downloader downloader = new Downloader(this);
    private CrashReporter reporter = new CrashReporter("Kubithon", new File(Downloader.FOLDER, "crashes"));

    public LauncherPanel(boolean translucent)
    {
        this.setLayout(null);
        this.setBackground(Swinger.TRANSPARENT);

        if (translucent)
        {
            this.setBackground(Swinger.TRANSPARENT);
            this.setOpaque(false);
        }
        else
        {
            this.setBackground(LauncherFrame.BACKGROUND);
        }

        bar.setStringPainted(true);
        bar.setStringColor(Color.WHITE);
        bar.setString("Veuillez patienter...");
        bar.setBounds(0, 85, 300, 15);
        this.add(bar);

        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            downloader.start();
                        }
                        catch (IOException e)
                        {
                            reporter.catchError(e, "Impossible de mettre a jour les mods !");
                            System.exit(1);
                        }

                        JOptionPane.showMessageDialog(LauncherPanel.this, "Les mods ont bien ete installes");

                        bar.setString("Telechargement du fichier de version");

                        File version = new File(Downloader.MINECRAFT_FOLDER, "versions/Kubithon/Kubithon.json");
                        version.getParentFile().mkdirs();

                        try
                        {
                            IOUtils.copy(new URL(Downloader.VERSION_FILE).openStream(), new FileOutputStream(version));
                        }
                        catch (IOException e)
                        {
                            reporter.catchError(e, "Impossible de télécharger le fichier de version");
                        }

                        JOptionPane.showMessageDialog(LauncherPanel.this, "Le fichier de version a bien ete telecharge");

                        bar.setString("Installation du profile");

                        File launcherProfiles = new File(Downloader.MINECRAFT_FOLDER, "launcher_profiles.json");

                        try
                        {
                            JSONObject object = new JSONObject(IOUtils.toString(new FileInputStream(launcherProfiles), Charset.defaultCharset()));
                            JSONObject profiles = object.getJSONObject("profiles");

                            if (!profiles.has("Kubithon"))
                            {
                                JSONObject kubithon = new JSONObject("{ \"name\": \"Kubithon\", \"gameDir\": \"" + Downloader.FOLDER.getAbsolutePath().replace("\\", "\\\\") + "\", \"lastVersionId\": \"Kubithon\", \"useHopperCrashService\": false }");
                                profiles.put("Kubithon", kubithon);

                                object.put("selectedProfile", "Kubithon");

                                BufferedWriter writer = new BufferedWriter(new FileWriter(launcherProfiles));
                                object.write(writer);

                                IOUtils.closeQuietly(writer);

                                JOptionPane.showMessageDialog(LauncherPanel.this, "Le profile a bien ete enregistre");
                            }
                            else
                            {
                                JOptionPane.showMessageDialog(LauncherPanel.this, "Le profile etait deja enregiste");
                            }
                        }
                        catch (IOException e)
                        {
                            reporter.catchError(e, "Impossible de modifier les profils du launcher !\nVous devez avoir installé Minecraft 1.10.2 avant de lancer l'installeur !");
                        }

                        System.exit(0);
                    }
                }.start();
            }
        });
    }

    public AbstractProgressBar getBar()
    {
        return bar;
    }

    @Override
    protected void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);

        graphics.drawImage(background, 0, 0, background.getWidth(this), background.getHeight(this), this);
    }
}
