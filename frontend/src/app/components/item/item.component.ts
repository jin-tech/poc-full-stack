export class ItemComponent {
  items: any[] = [];
  selectedItem: any = null;

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.getItems();
  }

  getItems() {
    this.apiService.getItems().subscribe(data => {
      this.items = data;
    });
  }

  selectItem(item: any) {
    this.selectedItem = item;
  }

  createItem(item: any) {
    this.apiService.createItem(item).subscribe(newItem => {
      this.items.push(newItem);
      this.selectedItem = null;
    });
  }

  updateItem(item: any) {
    this.apiService.updateItem(item).subscribe(updatedItem => {
      const index = this.items.findIndex(i => i.id === updatedItem.id);
      this.items[index] = updatedItem;
      this.selectedItem = null;
    });
  }

  deleteItem(item: any) {
    this.apiService.deleteItem(item.id).subscribe(() => {
      this.items = this.items.filter(i => i.id !== item.id);
      this.selectedItem = null;
    });
  }
}