import java.util.Scanner;
/**
 *
 * @author r-n
 * Muhammad Rizqi Nur
 * H76217063
 */

public class ShoppingCart{
	
	public static class ShoppingCartException extends Exception{
		public ShoppingCartException(String message){
			super(message);
		}
	}
	public static class ItemType{
		public int id;
		public String name;
		public int price;
		
		public ItemType(int id, String name, int price){
			this.id = id;
			this.name = name;
			this.price = price;
		}
	}
	public static class Item{
		
		public static ItemType[] types = new ItemType[]{
			new ItemType(
				0,
				"Cheetos",
				1000
			),
			new ItemType(
				1,
				"Kecap",
				12000
			),
			new ItemType(
				2,
				"Saus Tomat",
				11000
			),
			new ItemType(
				3,
				"Aqua",
				3000
			),
			new ItemType(
				4,
				"Roti Tawar",
				15000
			)
		};
		
		public ItemType type;
		public int qty;
		
		public Item(ItemType type, int qty){
			this.type = type;
			this.qty = qty;
		}
		
		public long calculate(){
			return qty * type.price;
		}
		
		public void addItem(Item item) throws ShoppingCartException{
			if(item.type != this.type){
				throw new ShoppingCartException ("Item yang dikembalikan berbeda");
			}
			this.qty += item.qty;
			item.qty = 0;
		}
		
		public Item takeItem(int qty) throws ShoppingCartException{
			if (qty > this.qty){
				throw new ShoppingCartException("Jumlah item yang ingin dibeli melebihi jumlah item yang tersedia untuk item '" + type.name + "'");
			}
			if(qty == 0){
				throw new ShoppingCartException("Qty tidak boleh nol");
			}
			this.qty -= qty;
			return new Item(
				this.type,
				qty
			);
		}
	}
	static Item[] availableItems;
	public static void init(){
		int n = Item.types.length;
		availableItems = new Item[n];
		for (int i = 0; i < n; ++i){
			Item.types[i].id = i;
			availableItems[i] = new Item(
				Item.types[i],
				1000
			);
		}
	}
	public static class Cart{
		public Item[] items;
		
		public boolean checkedOut = false;
		
		public Cart(){
			items = new Item[availableItems.length];
		}
		
		public void print(){
			int n = items.length;
			if(isEmpty()){
				System.out.println("Keranjang kosong");
				return;
			}
			long subtotal2 = 0;
			int j = 0;
			System.out.println("No.  Nama Item\tQty  \t  Harga/pc  \t      Subtotal1");
			for (int i = 0; i < n; ++i){
				Item item = items[i];
				if(item == null || item.qty <= 0) continue;
				long subtotal1 = item.calculate();
				System.out.printf("%2d. %10s\t%3d x\t%10d =\t%15d\n", ++j, item.type.name, item.qty, item.type.price, subtotal1);
				subtotal2 += subtotal1;
			}
			System.out.printf("Subtotal  \t\t\t   =\t%15d\n", subtotal2);
			long tax = subtotal2/10;
			System.out.printf("PPn       \t\t\t   =\t%15d\n", tax);
			long total = subtotal2 + tax;
			System.out.printf("Total     \t\t\t   =\t%15d\n", total);
		}
		
		public long calculateNoPPn(){
			int n = items.length;
			long subtotal2 = 0;
			for (int i = 0; i < n; ++i){
				Item item = items[i];
				if(item == null || item.qty <= 0) continue;
				long subtotal1 = item.calculate();
				subtotal2 += subtotal1;
			}
			return subtotal2;
		}
		
		public boolean isEmpty(){
			return ShoppingCart.isEmpty(items);
		}
		
		public long calculate(){
			long subtotal2 = calculateNoPPn();
			long tax = subtotal2*taxPercentage/100;
			long total = subtotal2 + tax;
			return total;
		}
		
		public void checkOut() throws ShoppingCartException{
			if(checkedOut){
				throw new ShoppingCartException("Keranjang sudah dibayar");
			}
			if(isEmpty()){
				throw new ShoppingCartException("Keranjang kosong");
			}
			checkedOut = true;
		}
		
		public long returnItem(int id, int qty) throws ShoppingCartException{
			if(checkedOut){
				throw new ShoppingCartException("Keranjang sudah dibayar");
			}
			if (id < 1 || id > items.length){
				throw new ShoppingCartException("Item tidak ada");
			}
			id = findId(items, id);
			if (id < 0){
				throw new ShoppingCartException("Item tidak ada di keranjang");
			}
			Item item = items[id];
			long ret;
			if(qty < item.qty){
				item = item.takeItem(qty);
				ret = item.calculate();
				availableItems[id].addItem(item);
			}else{
				ret = item.calculate();
				availableItems[id].addItem(item);
				items[id] = null;
			}
			return -ret;
			
		}
		
		public void returnAll() throws ShoppingCartException{
			if(checkedOut){
				throw new ShoppingCartException("Keranjang sudah dibayar");
			}
			int n = items.length;
			long subtotal2 = 0;
			for (int i = 0; i < n; ++i){
				Item item = items[i];
				if(item == null || item.qty <= 0) continue;
				availableItems[i].addItem(item);
			}
		}
		
		public long addItem(int id, int qty) throws ShoppingCartException{
			if(checkedOut){
				throw new ShoppingCartException("Keranjang sudah dibayar");
			}
			if (id < 1 || id > availableItems.length){
				throw new ShoppingCartException("Item tidak ada");
			}
			id = findId(availableItems, id);
			if (id < 0){
				throw new ShoppingCartException("Item tidak ada di keranjang");
			}
			Item item = availableItems[id].takeItem(qty);
			long ret = item.calculate();
			Item old = items[id];
			if (old == null){
				items[id] = item;
			}else{
				old.addItem(item);
			}
			return ret;
		}
		
	}
	
	public static int findId(Item[] items, int index){
		int n = items.length;
		int id = index;
		for (int i = 0; i < n; ++i){
			Item item = items[i];
			if(item == null || item.qty <= 0) continue;
			if(--id==0){
				return item.type.id;
			}
		}
		return -1;
	}
	
	public static int taxPercentage = 10;
	
	public static boolean isEmpty(Item[] items){
		int n = items.length;
		for (int i = 0; i < n; ++i){
			Item item = items[i];
			if(item == null || item.qty <= 0) continue;
			return false;
		}
		return true;
	}
	
	public static void printAvailableItems(){
		int n = availableItems.length;
		if(isEmpty(availableItems)){
			System.out.println("Tidak ada item yang tersedia");
			return;
		}
		int j = 0;
		System.out.println("No.  Nama Item\tQty  \t  Harga/pc");
		for (int i = 0; i < n; ++i){
			Item item = availableItems[i];
			if(item == null || item.qty <= 0) continue;
			System.out.printf("%2d. %10s\t%3d x\t%10d\n", ++j, item.type.name, item.qty, item.type.price);
		}
	}
	public static void printMenu(){
		System.out.println("Toko");
		System.out.println("1. Beli barang");
		System.out.println("2. Lihat keranjang");
		System.out.println("3. Kembalikan barang");
		System.out.println("4. Kembalikan semua barang");
		System.out.println("5. Bayar");
	}
	
	static Cart[] transactionHistory = new Cart[100];
	static int transactionCount = 0;
	
	public static void checkOut(Cart cart) throws ShoppingCartException{
		cart.checkOut();
		transactionHistory[transactionCount++] = cart;
	}
	public static void main(String[] args) throws ShoppingCartException{
		init();
		Scanner sc = new Scanner(System.in);
		Cart cart = new Cart();
		do{
			try{
				printMenu();
				System.out.print("Pilihan anda: ");
				int c = sc.nextInt();
				System.out.println();
				switch(c){
					case 1:{
						printAvailableItems();
						System.out.print("Pilihan anda: ");
						int d = sc.nextInt();
						if (d <= 0 || d > availableItems.length){
							throw new ShoppingCartException("Item tidak ada");
						}
						int id = findId(availableItems, d);
						if (id<0 || availableItems[id].qty==0){
							throw new ShoppingCartException("Item tidak ada di keranjang");
						}
						Item item = availableItems[id];
						int aQty = item.qty;
						System.out.printf("Berapa banyak yang ingin anda beli? (max %d) ", aQty);
						int qty = sc.nextInt();
						if (qty > aQty){
							System.out.printf("Tidak bisa membeli lebih banyak dari yang tersedia (%d)\n\n", aQty);
							break;
						}
						System.out.printf("Menambahkan %s sebanyak %d buah ke dalam keranjang.\n", item.type.name, qty);
						System.out.printf("Total di keranjang bertambah %d menjadi %d sebelum PPn dan %d setelah PPn\n\n", cart.addItem(d, qty),cart.calculateNoPPn(), cart.calculate());
						break;
					}
					case 2:{
						cart.print();
						System.out.println();
						break;
					}
					case 3:{
						cart.print();
						System.out.print("Pilih item yang ingin dikembalikan: ");
						int d = sc.nextInt();
						if (d <= 0 || d > availableItems.length){
							throw new ShoppingCartException("Item tidak ada");
						}
						int id = findId(cart.items, d);
						if (id<0 || cart.items[id] == null || cart.items[id].qty==0){
							throw new ShoppingCartException("Item tidak ada di keranjang. d=" + d + " id= " + id);
						}
						Item item = cart.items[id];
						int cQty = item.qty;
						System.out.printf("Berapa banyak yang ingin anda kembalikan? (max %d) ", cQty);
						int qty = sc.nextInt();
						if (qty > cQty){
							System.out.printf("Tidak bisa mengembalikan lebih banyak dari yang ada di keranjang (%d)\n", cQty);
							break;
						}
						
						System.out.printf("Menambahkan %s sebanyak %d buah ke dalam keranjang.\n", item.type.name, qty);
						System.out.printf("Total di keranjang berkurang %d menjadi %d sebelum PPn dan %d setelah PPn\n\n", -cart.returnItem(d, qty), cart.calculateNoPPn(), cart.calculate());
						break;
					}
					case 4:{
						System.out.print("Apa anda yakin ingin mengembalikan semua item? (Y) ");
						String y = sc.next();
						if(y.equals("Y") || y.equals("y")){
							cart.returnAll();
							System.out.println("Keranjang sudah dikosongkan\n");
						}else{
							System.out.println("Keranjang tidak jadi dikosongkan\n");
						}
						break;
					}
					case 5:{
						if(cart.isEmpty()){
							System.out.println("Keranjang kosong");
							break;
						}
						cart.print();
						System.out.print("Apa sudah dibayar? (Y) ");
						String y = sc.next();
						if(y.equals("Y") || y.equals("y")){
							checkOut(cart);
							cart = new Cart();
							System.out.println("Keranjang sudah dibayar\n");
						}else{
							System.out.println("Keranjang belum dibayar\n");
						}
						break;
					}
					default:{
						System.out.printf("Menu no.%d tidak ada\n\n", c);
					}
				}
			}catch(ShoppingCartException ex){
				System.out.println(ex.getMessage());
				System.out.println();
			}
		}while(true);
	}
}