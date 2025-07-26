module.exports = {
  directConnect: true,
  framework: 'jasmine',
  specs: ['./src/**/*.e2e-spec.ts'],
  capabilities: {
    browserName: 'chrome',
  },
  jasmineNodeOpts: {
    defaultTimeoutInterval: 30000,
  },
  onPrepare: () => {
    require('ts-node').register({
      project: 'e2e/tsconfig.e2e.json',
    });
  },
  baseUrl: 'http://localhost:4200/',
};