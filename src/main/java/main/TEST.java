package main;

public class TEST {

	public static void main(String[] args) {
		final RoleListProviderInterface provider = new RoleListProvider();
		provider.loadRoles();
		provider.print();
	}

}
