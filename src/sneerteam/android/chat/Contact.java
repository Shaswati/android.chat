package sneerteam.android.chat;

public class Contact {
	
	private String nome;
	
	public Contact(String nome) {
		this.nome = nome;
	}
	
	@Override public String toString() {
		return nome;
	}
	
}

