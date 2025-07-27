import { test, expect } from '@playwright/test';

test.describe('POC Application', () => {
  test.beforeEach(async ({ page }) => {
    // Mock the API to avoid backend dependency
    await page.route('**/api/items', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([])
      });
    });
    
    await page.goto('/');
  });

  test('should display the application title', async ({ page }) => {
    await expect(page.locator('h1')).toContainText('POC Project');
  });

  test('should navigate to items page', async ({ page }) => {
    await expect(page.locator('h2')).toContainText('Items Management');
  });

  test('should show add new item button', async ({ page }) => {
    await expect(page.locator('button:has-text("Add New Item")')).toBeVisible();
  });

  test('should show empty state when no items', async ({ page }) => {
    await expect(page.locator('text=No items found')).toBeVisible();
  });

  test('should open create form when clicking add button', async ({ page }) => {
    await page.click('button:has-text("Add New Item")');
    await expect(page.locator('h3:has-text("Create New Item")')).toBeVisible();
    await expect(page.locator('input[formControlName="name"]')).toBeVisible();
  });

  test('should cancel form creation', async ({ page }) => {
    // Open create form
    await page.click('button:has-text("Add New Item")');
    
    // Cancel form
    await page.click('button:has-text("Cancel")');
    
    // Form should be hidden
    await expect(page.locator('h3:has-text("Create New Item")')).not.toBeVisible();
  });

  test('should validate form fields', async ({ page }) => {
    // Open create form
    await page.click('button:has-text("Add New Item")');
    
    // Try to submit empty form
    await page.click('button[type="submit"]');
    
    // Should show validation error
    await expect(page.locator('text=Name is required')).toBeVisible();
  });
});
