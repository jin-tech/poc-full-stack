import { test, expect } from '@playwright/test';

test.describe('Item Management E2E', () => {
  test.beforeEach(async ({ page }) => {
    // Mock API responses for consistent testing
    await page.route('**/api/items', async route => {
      if (route.request().method() === 'GET') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify([
            { id: 1, name: 'Sample Item 1', description: 'First sample item' },
            { id: 2, name: 'Sample Item 2', description: 'Second sample item' }
          ])
        });
      } else if (route.request().method() === 'POST') {
        const requestBody = await route.request().postDataJSON();
        await route.fulfill({
          status: 201,
          contentType: 'application/json',
          body: JSON.stringify({
            id: 3,
            ...requestBody
          })
        });
      }
    });

    await page.route('**/api/items/*', async route => {
      if (route.request().method() === 'DELETE') {
        await route.fulfill({
          status: 204
        });
      } else if (route.request().method() === 'PUT') {
        const requestBody = await route.request().postDataJSON();
        const itemId = route.request().url().split('/').pop();
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            id: parseInt(itemId!),
            ...requestBody
          })
        });
      }
    });

    await page.goto('/items');
  });

  test('should display existing items', async ({ page }) => {
    await expect(page.locator('text=Sample Item 1')).toBeVisible();
    await expect(page.locator('text=Sample Item 2')).toBeVisible();
  });

  test('should create a new item successfully', async ({ page }) => {
    // Click add button
    await page.click('button:has-text("Add New Item")');
    
    // Fill form
    await page.fill('input[formControlName="name"]', 'New Test Item');
    await page.fill('textarea[formControlName="description"]', 'New test description');
    
    // Submit form
    await page.click('button[type="submit"]:has-text("Create")');
    
    // Wait for form to close
    await expect(page.locator('h3:has-text("Create New Item")')).not.toBeVisible();
  });

  test('should edit an existing item', async ({ page }) => {
    // Click edit button on first item
    await page.locator('button:has-text("Edit")').first().click();
    
    // Verify form is pre-filled
    await expect(page.locator('input[formControlName="name"]')).toHaveValue('Sample Item 1');
    
    // Update name
    await page.fill('input[formControlName="name"]', 'Updated Item Name');
    
    // Submit form
    await page.click('button[type="submit"]:has-text("Update")');
    
    // Form should close
    await expect(page.locator('h3:has-text("Edit Item")')).not.toBeVisible();
  });

  test('should delete an item with confirmation', async ({ page }) => {
    // Mock the confirm dialog
    page.on('dialog', async dialog => {
      expect(dialog.message()).toContain('Are you sure you want to delete');
      await dialog.accept();
    });
    
    // Click delete button
    await page.locator('button:has-text("Delete")').first().click();
  });

  test('should search items', async ({ page }) => {
    // Search for "Sample Item 1"
    await page.fill('input[placeholder="Search items..."]', 'Sample Item 1');
    
    // Should show matching item
    await expect(page.locator('text=Sample Item 1')).toBeVisible();
    
    // Should hide non-matching item
    await expect(page.locator('text=Sample Item 2')).not.toBeVisible();
  });

  test('should validate form fields', async ({ page }) => {
    // Open create form
    await page.click('button:has-text("Add New Item")');
    
    // Try to submit empty form
    await page.click('button[type="submit"]');
    
    // Should show validation error
    await expect(page.locator('text=Name is required')).toBeVisible();
    
    // Form should still be visible
    await expect(page.locator('h3:has-text("Create New Item")')).toBeVisible();
  });

  test('should handle loading states', async ({ page }) => {
    // Delay the API response to test loading state
    await page.route('**/api/items', async route => {
      await new Promise(resolve => setTimeout(resolve, 1000));
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([])
      });
    });

    await page.goto('/items');
    
    // Should show loading indicator
    await expect(page.locator('text=Loading items...')).toBeVisible();
  });
});
