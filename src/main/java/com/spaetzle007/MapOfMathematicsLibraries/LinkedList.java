package com.spaetzle007.MapOfMathematicsLibraries;
import java.util.ArrayList;

import java.io.IOException;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.DeleteErrorException;

public class LinkedList {
	private ArrayList<Linked> list;
	private DataHandler droppi;

	/**
	 * Konstruktor, der Datei einliest und LinkedList dementsprechend erstellt
	 */
	public LinkedList() throws AccessException, LinkedParseException {
		droppi=new DataHandler();
		String code = droppi.getMOMText();
		
		list = new ArrayList<Linked>();
		decodeXML(code);
		
		sort();
	}
	
	/**
	 * Speichert LinkedList in Datei
	 */
	public void saveList() throws DeleteErrorException, DbxException, IOException{
		droppi.uploadMOMtext(convertToXML());
	}
	public void sicherungskopie() {
		droppi.sicherungskopie();
	}
	
	public Linked get(int i) {return list.get(i);}
	public int size() {return list.size();}
	public void remove(String name) {
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getName().equals(name)) {
				list.remove(i);
			}
		}
	}
	public void add(Linked link) {list.add(link);}
	public void set(int pos, Linked link) {list.set(pos, link);}
	
	public int search(String name) {
		int pos=-1;
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getName().equals(name)) {
				pos=i;
			}
		}
		return pos;
	}
	
	/**
	 * Anhand von Supnames equalnames und subnames berechnen
	 * equalnames berechnen: Bei gleichem Supname aktueller Name als Link eintragen
	 * subnames berechnen: Von Subnames aus machen
	 * Name und Supname unverändert -> Arbeite nur damit
	 */
	/*		//Kann weg
	public void calculateConnecteds() {
		for(int i=0; i<list.size(); i++) {
			list.get(i).clearConnecteds();
		}
		for(int i=0; i<list.size(); i++) {		//Iteration über LinkedList
			String sup=list.get(i).getSupLink();
			
			//subnames von unten
			if(!list.get(i).getName().equals("Start")) {
				list.get(search(sup)).addConnected(new LinkedString(list.get(i).getName(), (byte)1));
			} 
			
			//equalnames hin und zurück
			for(int j=i+1; j<list.size(); j++) {
				if(list.get(i).getSupName().equals(list.get(j).getSupName()) && !list.get(i).getName().equals("Start") && !list.get(j).getName().equals("Start")) {
					list.get(i).addConnected(new LinkedString(list.get(j).getName(), (byte)0));
					list.get(j).addConnected(new LinkedString(list.get(i).getName(), (byte)0));
				}
			}
			
			//crossnames (zur Sicherheit)
			ArrayList<LinkedString> cross=list.get(i).getCrossLinks();
			for(int j=0; j<cross.size(); j++) {
				list.get(search(cross.get(j).getName())).addConnected(new LinkedString(cross.get(j).getName(), (byte)2));
			}
			list.get(i).sortConnected();
		}
		list.get(search("Start")).addConnected(new LinkedString("Start", (byte)0));
	}
	*/
	/**
	 * Verknüpfungen aktualisieren
	 * (Aber nur für einen Linked)
	 */
	/*//Kann weg
	public void calculateConnecteds(Linked actual) {
		list.get(search(actual.getSupLink())).addConnected(new LinkedString(actual.getName(), (byte)1));
		
		for(int i=0; i<actual.getConnecteds().size(); i++) {
			//Equalnames: Bijektiv machen(mit gleichem supname)
			if(actual.getConnecteds().get(i).getType()==(byte)0) {
				list.get(search(actual.getConnecteds().get(i).getName())).addConnected(new LinkedString(actual.getName(), (byte)0));
			}
			//Crossnames: Bijektiv machen
			if(actual.getConnecteds().get(i).getType()==(byte)2) {
				list.get(search(actual.getConnecteds().get(i).getName())).addConnected(new LinkedString(actual.getName(), (byte)2));
			}
		}
	}
	*/
	/*
	public void changeConnectedsName(Linked before, Linked actual) {
		if(!before.getName().equals(actual.getName())) {
			for(int i=0; i<list.size(); i++) {
				for(int j=0; j<list.get(i).getConnecteds().size(); j++) {
					if(list.get(i).getConnecteds().get(j).getName().equals(before.getName())) {
						list.get(i).setConnected(j, new LinkedString(actual.getName(), list.get(i).getConnecteds().get(j).getType()));
					}
				}
			}
		}
	}
	*/
	
	//Sinn davon?
	public void changeConnectedsName(Linked before, Linked actual) {
		if(!before.getName().equals(actual.getName())) {
			for(int i=0; i<list.size(); i++) {
				for(int j=0; j<list.get(i).getConnecteds().size(); j++) {
					if(list.get(i).getConnecteds().get(j).equals(before.getName())) {
						list.get(i).setConnected(j, actual.getName());
					}
				}
			}
		}
	}
	
	public String convertToXML() {
		String ret="";
		ret+="<LinkedList>\n";
		for(int i=0; i<list.size(); i++) {
			ret+=list.get(i).convertToXML();
		}
		ret+="</LinkedList>";
		return ret;
	}
	
	/**
	 * Keine '\n's angezeigt, da in dem von gedit enthaltenen code keine Leerzeilen enthalten sind
	 * Lösche alle Tabs - Diese sind nur zur Übersicht im xml-Format
	 */
	private void decodeXML(String input) throws LinkedParseException {
		String str=input.replace("\t", "");
		
		int i=0;
		//Eingangsstring testen
		if(str.substring(0, "<LinkedList>".length()).equals("<LinkedList>")) {
			i+="<LinkedList>".length();
		} else {
			throw new LinkedParseException("Falsches Format");
		}
		
		while(i<str.length()-"</LinkedList>".length()) {
			int i0=i;
			if(str.substring(i, i+"<Linked>".length()).equals("<Linked>")) {
				i+="<Linked>".length();
			} else {
				throw new LinkedParseException("Falsches Format");
			}
			
			while(!str.substring(i, i+"</Linked>".length()).equals("</Linked>")) {
				i++;
			}
			i+="</Linked>".length();
			
			list.add(new Linked(str.substring(i0, i)));
			
			//Leere Einträge direkt löschen
			if(list.get(list.size()-1).getName().equals("")) {
				list.remove(list.get(list.size()-1));
			}
		}
		//Ausgangsstring testen
		if(!str.substring(i, i+"</LinkedList>".length()).equals("</LinkedList>")) {
			throw new LinkedParseException("Falsches Format");
		}
	}
	
	private void sort() {
		ArrayList<Linked> output=new ArrayList<Linked>();
		Linked erster;
		ArrayList<Linked> speicher=list;
		while(!speicher.isEmpty()) {
			erster=speicher.get(0);
			for(int j=1; j<speicher.size(); j++) {
				if(speicher.get(j).getName().compareToIgnoreCase(erster.getName())>0) {
					continue;
				} else {
					erster=speicher.get(j);
					continue;
				}
			}
			output.add(erster);
			speicher.remove(erster);
		}
		list=output;
	}
	public ArrayList<String> getSubLinks(Linked actual) {
		ArrayList<String> ret=new ArrayList<String>();
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getSupLink().equals(actual.getName()) && !list.get(i).getName().equals("Start")) {
				ret.add(list.get(i).getName());
			} 
		}
		return ret;
	}
	public ArrayList<String> getEqualLinks(Linked actual) {
		ArrayList<String> ret=new ArrayList<String>();
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getSupLink().equals(actual.getSupLink()) && !list.get(i).getName().equals("Start")) {
				ret.add(list.get(i).getName());
			}
		}
		return ret;
	}
}
