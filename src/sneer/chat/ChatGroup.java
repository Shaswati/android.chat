package sneer.chat;

import java.util.List;

import sneer.snapi.Contact;

public class ChatGroup {

	public String name;
	
	public List<Contact> contacts;
	
	public ChatGroup(List<Contact> contacts){
		this.contacts = contacts;
	}
	
	
}
