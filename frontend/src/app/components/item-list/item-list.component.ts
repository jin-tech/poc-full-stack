import { Component, OnInit, signal, computed, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Item } from '../../models/item.model';

@Component({
  selector: 'app-item-list',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="min-h-screen bg-gray-50 p-8">
      <div class="max-w-6xl mx-auto">
        <!-- Header -->
        <div class="mb-8">
          <h2 class="text-3xl font-bold text-gray-900 mb-4">Items Management</h2>
          <div class="flex flex-col sm:flex-row gap-4">
            <button
              (click)="showCreateForm.set(true)"
              class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg font-medium transition-colors shadow-sm"
            >
              Add New Item
            </button>
            <div class="relative flex-1">
              <input
                type="text"
                placeholder="Search items..."
                #searchInput
                (input)="searchTerm.set(searchInput.value)"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 pl-10"
              />
              <svg class="absolute left-3 top-3.5 h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>
        </div>

        <!-- Create/Edit Form -->
        @if (showCreateForm() || editingItem()) {
          <div class="bg-white rounded-lg shadow-lg border border-gray-200 p-6 mb-8">
            <h3 class="text-xl font-semibold text-gray-900 mb-6">
              {{ editingItem() ? 'Edit Item' : 'Create New Item' }}
            </h3>
            <form [formGroup]="itemForm" (ngSubmit)="onSubmit()">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Name *</label>
                  <input
                    type="text"
                    formControlName="name"
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    [class.border-red-500]="itemForm.get('name')?.invalid && itemForm.get('name')?.touched"
                    placeholder="Enter item name"
                  />
                  @if (itemForm.get('name')?.invalid && itemForm.get('name')?.touched) {
                    <p class="text-red-500 text-sm mt-2">Name is required</p>
                  }
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Description</label>
                  <textarea
                    formControlName="description"
                    rows="3"
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    placeholder="Enter item description (optional)"
                  ></textarea>
                </div>
              </div>
              <div class="flex gap-4 mt-6">
                <button
                  type="submit"
                  [disabled]="itemForm.invalid || loading()"
                  class="bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white px-6 py-3 rounded-lg font-medium transition-colors shadow-sm"
                >
                  @if (loading()) {
                    <span class="flex items-center">
                      <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                      Saving...
                    </span>
                  } @else {
                    {{ editingItem() ? 'Update Item' : 'Create Item' }}
                  }
                </button>
                <button
                  type="button"
                  (click)="cancelForm()"
                  class="bg-gray-200 hover:bg-gray-300 text-gray-700 px-6 py-3 rounded-lg font-medium transition-colors"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        }

        <!-- Loading State -->
        @if (loading() && !items().length && !showCreateForm()) {
          <div class="text-center py-12">
            <div class="inline-flex items-center px-4 py-2 font-semibold leading-6 text-sm shadow rounded-md text-white bg-blue-500">
              <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Loading items...
            </div>
          </div>
        }

        <!-- Items Grid -->
        @if (filteredItems().length > 0) {
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            @for (item of filteredItems(); track item.id) {
              <div class="bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow border border-gray-200">
                <div class="p-6">
                  <h3 class="text-lg font-semibold text-gray-900 mb-2">{{ item.name }}</h3>
                  <p class="text-gray-600 mb-4 text-sm">{{ item.description || 'No description provided' }}</p>
                  <div class="flex gap-2">
                    <button
                      (click)="editItem(item)"
                      class="bg-yellow-500 hover:bg-yellow-600 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
                    >
                      Edit
                    </button>
                    <button
                      (click)="deleteItem(item)"
                      class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
                    >
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            }
          </div>
        } @else if (!loading()) {
          <div class="text-center py-16">
            <svg class="mx-auto h-16 w-16 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <h3 class="text-lg font-medium text-gray-900 mb-2">No items found</h3>
            <p class="text-gray-500 mb-6">
              {{ searchTerm() ? 'Try adjusting your search criteria.' : 'Get started by creating your first item.' }}
            </p>
            @if (!searchTerm()) {
              <button
                (click)="showCreateForm.set(true)"
                class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg font-medium transition-colors shadow-sm"
              >
                Create First Item
              </button>
            }
          </div>
        }
      </div>
    </div>
  `,
  styles: []
})
export class ItemListComponent implements OnInit {
  private readonly apiService = inject(ApiService);
  private readonly fb = inject(FormBuilder);

  // Signals for state management
  items = signal<Item[]>([]);
  loading = signal(false);
  searchTerm = signal('');
  showCreateForm = signal(false);
  editingItem = signal<Item | null>(null);

  // Computed derived state
  filteredItems = computed(() => {
    const term = this.searchTerm().toLowerCase();
    if (!term) return this.items();
    
    return this.items().filter(item => 
      item.name.toLowerCase().includes(term) || 
      (item.description?.toLowerCase().includes(term) ?? false)
    );
  });

  // Reactive form
  itemForm: FormGroup = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    description: ['']
  });

  ngOnInit(): void {
    this.loadItems();
  }

  private loadItems(): void {
    this.loading.set(true);
    this.apiService.getAllItems().subscribe({
      next: (items) => {
        this.items.set(items);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading items:', error);
        this.loading.set(false);
      }
    });
  }

  onSubmit(): void {
    if (this.itemForm.valid) {
      this.loading.set(true);
      const formValue = this.itemForm.value;
      
      if (this.editingItem()) {
        // Update existing item
        const itemId = this.editingItem()!.id!;
        this.apiService.updateItem(itemId, formValue).subscribe({
          next: (updatedItem) => {
            this.items.update(items => 
              items.map(item => item.id === itemId ? updatedItem : item)
            );
            this.cancelForm();
            this.loading.set(false);
          },
          error: (error) => {
            console.error('Error updating item:', error);
            this.loading.set(false);
          }
        });
      } else {
        // Create new item
        this.apiService.createItem(formValue).subscribe({
          next: (newItem) => {
            this.items.update(items => [...items, newItem]);
            this.cancelForm();
            this.loading.set(false);
          },
          error: (error) => {
            console.error('Error creating item:', error);
            this.loading.set(false);
          }
        });
      }
    }
  }

  editItem(item: Item): void {
    this.editingItem.set(item);
    this.showCreateForm.set(false);
    this.itemForm.patchValue({
      name: item.name,
      description: item.description || ''
    });
  }

  deleteItem(item: Item): void {
    if (confirm(`Are you sure you want to delete "${item.name}"?`)) {
      this.loading.set(true);
      this.apiService.deleteItem(item.id!).subscribe({
        next: () => {
          this.items.update(items => items.filter(i => i.id !== item.id));
          this.loading.set(false);
        },
        error: (error) => {
          console.error('Error deleting item:', error);
          this.loading.set(false);
        }
      });
    }
  }

  cancelForm(): void {
    this.showCreateForm.set(false);
    this.editingItem.set(null);
    this.itemForm.reset();
  }
}
