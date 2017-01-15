package fr.ironcraft.kubithon.launcher;

import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.util.WindowMover;
import java.awt.Color;
import javax.swing.JFrame;

public class LauncherFrame extends JFrame
{
    public static final Color BACKGROUND = new Color(60, 105, 148);

    private WindowMover mover = new WindowMover(this);

    public LauncherFrame()
    {
        this.setTitle("Kubithon");
        this.setUndecorated(true);
        this.setSize(300, 100);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        boolean translucent = false;
        try
        {
            translucent = getGraphicsConfiguration().isTranslucencyCapable();
        }
        catch (Throwable e)
        {
        }

        if (translucent)
        {
            this.setBackground(Swinger.TRANSPARENT);
            this.setOpacity(0.0F);
        }
        else
        {
            this.setBackground(Color.WHITE);
        }

        this.addMouseListener(mover);
        this.addMouseMotionListener(mover);

        this.setContentPane(new LauncherPanel(translucent));
    }

    public static void main(String[] args)
    {
        Swinger.setSystemLookNFeel();
        Swinger.setResourcePath("/res");

        Downloader.FOLDER.mkdirs();

        LauncherFrame frame = new LauncherFrame();
        frame.setVisible(true);
    }
}
