package sneer.chat;


public class Contact {
	
	private final String publicKey;
	private final String nickname;
	
	public Contact(String publicKey, String nickname) {
		this.publicKey = publicKey;
		this.nickname = nickname;
	}

	public String getPublicKey() {
		return publicKey;
	}
	
	public String getNickname() {
		return nickname;
	}

	@Override public String toString() {
		return nickname;
	}

	@Override
	public int hashCode() {
		return publicKey.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contact other = (Contact) obj;
		if (publicKey == null) {
			if (other.publicKey != null)
				return false;
		} else if (!publicKey.equals(other.publicKey))
			return false;
		return true;
	}

}
