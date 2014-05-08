package sneerteam.android.chat;

public class Contact {
	
	private String public_key;
	private String name;
	private String nickname;
	
	public Contact(String public_key, String nickname) {
		this.public_key = public_key;
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
		return nickname;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((nickname == null) ? 0 : nickname.hashCode());
		result = prime * result
				+ ((public_key == null) ? 0 : public_key.hashCode());
		return result;
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nickname == null) {
			if (other.nickname != null)
				return false;
		} else if (!nickname.equals(other.nickname))
			return false;
		if (public_key == null) {
			if (other.public_key != null)
				return false;
		} else if (!public_key.equals(other.public_key))
			return false;
		return true;
	}
}
