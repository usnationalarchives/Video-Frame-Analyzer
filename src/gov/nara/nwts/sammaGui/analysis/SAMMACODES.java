package gov.nara.nwts.sammaGui.analysis;

/**
 * Enumeration representing the frame-level properties reported in the Samma XML file
 * @author TBrady
 *
 */
public enum SAMMACODES {
	IX("Index"),
	TC("Frame Count"),
	IN("Input Present"),
	TT("Timecode"),
	TB("Timecode User Groups"),
	CB("Color Present"), 
	RF("RF Level"), 
	RW("RF Level Warning"), 
	RA("RF Level Alarm"), 
	HW("RF Head Difference Warning"),
	DL("DOC Length"), 
	DN("DOC Number"), 
	DW("DOC Warning"), 
	DA("DOC Alarm"), 
	MH("Missing H-Pulse Count"), 
	NR("Noise Level"), 
	MD("Motion Detect"),
	LA("Luma Average"), 
	LP("Luma Peak"), 
	UP("Pb Max"), 
	UM("Pb Min"), 
	UA("Pb Average"), 
	VP("Pr Max"), 
	VM("Pr Min"), 
	VA("Pr Average"), 
	A1("Ch1 Audio Average"), 
	A2("Ch2 Audio Average"),
	A3("Ch3 Audio Average"), 
	A4("Ch4 Audio Average"), 
	P1("Ch1 Peak"), 
	P2("Ch2 Peak"), 
	P3("Ch3 Peak"), 
	P4("Ch4 Peak"), 
	S1("Ch1 Silence"), 
	S2("Ch2 Silence"), 
	S3("Ch3 Silence"), 
	S4("Ch4 Silence"), 
	SV("Servo Lock");
	
	String name;
	public String code;
	SAMMACODES(String s) {
		name = s;
		code = super.toString();
	}
	
	public String toString() {
		return code +" " + name;
	}
}
