package fr.ironcraft.ieinstaller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class Downloader extends Thread {
	private IEInstaller parent;
	public String endpoint;
	
	public Downloader(IEInstaller parent) {
		this.parent = parent;
		endpoint = "http://ironcraft.fr/ie_installer/";
	}
	
	@Override
	public void run() {
		try {
			FileUtils.deleteDirectory(IEInstaller.ironDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<String> fileList;
		InputStream in = null;
		
		try {
			in = new URL(this.endpoint + "md5sum").openStream();
			fileList = Arrays.asList(IOUtils.toString(in, "UTF-8").split("\n"));
		} catch(IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this.parent, "Erreur lors de la récupération de la liste des mods.", "Erreur", JOptionPane.ERROR_MESSAGE);
			return;
		} finally {
			if(in != null)
				IOUtils.closeQuietly(in);
		}
		
		for(Iterator<String> i = fileList.iterator(); i.hasNext();) {
			String[] mod = i.next().split(" ");
			String modName = mod[0].substring(5);
			File modFile = new File(IEInstaller.ironDir, mod[0]);
			
			this.parent.btn_installer.setText("Téléchargement de " + modName + "...");
			
			try {
				FileUtils.copyURLToFile(new URL(this.endpoint + mod[0]), modFile, 3000, 3000);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this.parent, "Erreur lors du téléchargement du mod : " + modName + ".", "Erreur", JOptionPane.ERROR_MESSAGE);
			}
			
			this.parent.btn_installer.setText("Vérification du téléchargement...");
			
			try {
				String calcmd5 = DigestUtils.md5Hex(FileUtils.readFileToByteArray(modFile));
				if(!calcmd5.equals(calcmd5)) {
					JOptionPane.showMessageDialog(this.parent, "Le fichier du mod : " + modName + " est invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
					modFile.delete();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.parent.btn_installer.setEnabled(true);
	}
}
