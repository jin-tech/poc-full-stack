import { browser, by, element } from 'protractor';

describe('POC Project End-to-End Tests', () => {
  beforeEach(() => {
    browser.get('/');
  });

  it('should display the title', () => {
    expect(browser.getTitle()).toEqual('POC Project');
  });

  it('should display items', () => {
    const itemList = element(by.css('.item-list'));
    expect(itemList.isPresent()).toBe(true);
  });

  it('should create a new item', () => {
    element(by.css('input[name="name"]')).sendKeys('New Item');
    element(by.css('textarea[name="description"]')).sendKeys('Description of new item');
    element(by.css('button[type="submit"]')).click();

    const itemList = element(by.css('.item-list'));
    expect(itemList.getText()).toContain('New Item');
  });

  it('should update an existing item', () => {
    element(by.css('.item-list .item:first-child .edit-button')).click();
    element(by.css('input[name="name"]')).clear().sendKeys('Updated Item');
    element(by.css('button[type="submit"]')).click();

    const itemList = element(by.css('.item-list'));
    expect(itemList.getText()).toContain('Updated Item');
  });

  it('should delete an item', () => {
    element(by.css('.item-list .item:first-child .delete-button')).click();
    const itemList = element(by.css('.item-list'));
    expect(itemList.getText()).not.toContain('Updated Item');
  });
});