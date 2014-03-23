package sneerteam.android.chat;

public class Contact {
	
	private String public_key;
	private String name;
	private String nickname;
	
	public Contact(String public_key, String name, String nickname) {
		this.public_key = public_key;
		this.name = name;
		this.nickname = nickname;
	}

	public String getPublicKey() {
		return public_key;
	}
	
	public void setPublicKey(String public_key) {
		this.public_key = public_key;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	@Override public String toString() {
		return name;
	}
}
