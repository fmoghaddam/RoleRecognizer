package main;

public class TEST {

	public static void main(String[] args) {
		final RoleListProvider provider = new RoleListProviderFileBased();
		provider.loadRoles();
		provider.print();
	}

}
