import { ITaxRate, NewTaxRate } from './tax-rate.model';

export const sampleWithRequiredData: ITaxRate = {
  id: 32485,
  country: 'Botswana',
  state: 'disintermediate',
  postcode: '1080p bottom-line In',
  city: 'Kingsport',
  rate: 'copying',
  name: 'Loan Kids hardware',
  compound: true,
  priority: 86175,
};

export const sampleWithPartialData: ITaxRate = {
  id: 63828,
  country: 'Wallis and Futuna',
  state: 'methodologies',
  postcode: 'Rubber',
  city: 'South Georgetteberg',
  rate: 'Forward olive Designer',
  name: 'out-of-the-box',
  compound: false,
  priority: 43078,
};

export const sampleWithFullData: ITaxRate = {
  id: 63661,
  country: 'Antarctica (the territory South of 60 deg S)',
  state: 'Practical eyeballs',
  postcode: 'SQL connect',
  city: 'McGlynnhaven',
  rate: 'yellow Awesome Cambridgeshire',
  name: 'Granite Egypt Sports',
  compound: false,
  priority: 10725,
};

export const sampleWithNewData: NewTaxRate = {
  country: 'Cuba',
  state: 'input local Berkshire',
  postcode: 'Ergonomic interfaces',
  city: 'West New York',
  rate: 'encoding',
  name: 'haptic',
  compound: true,
  priority: 92022,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
