package sneerteam.android.chat;

public class Contact {
	
	private String nome;
	
	public Contact(String nome) {
		this.nome = nome;
	}
	
	public String getNome() {
		return nome;
	}
	
	@Override public String toString() {
		return nome;
	}
	
}

