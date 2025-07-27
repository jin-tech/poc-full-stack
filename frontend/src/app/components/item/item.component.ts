import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-item',
  standalone: false,
  templateUrl: './item.component.html',
  styleUrls: ['./item.component.scss']
})
export class ItemComponent implements OnInit {
  items: any[] = [];
  selectedItem: any = null;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.getItems();
  }

  getItems(): void {
    this.apiService.getAllItems().subscribe((data: any) => {
      this.items = data;
    });
  }

  selectItem(item: any): void {
    this.selectedItem = item;
  }

  createItem(item: any): void {
    this.apiService.createItem(item).subscribe((newItem: any) => {
      this.items.push(newItem);
      this.selectedItem = null;
    });
  }

  updateItem(item: any): void {
    this.apiService.updateItem(item.id, item).subscribe((updatedItem: any) => {
      const index = this.items.findIndex(i => i.id === updatedItem.id);
      this.items[index] = updatedItem;
      this.selectedItem = null;
    });
  }

  deleteItem(item: any): void {
    this.apiService.deleteItem(item.id).subscribe(() => {
      this.items = this.items.filter(i => i.id !== item.id);
      this.selectedItem = null;
    });
  }
}