import { TestBed } from '@angular/core/testing';

import { LogStream } from './log-stream';

describe('LogStream', () => {
  let service: LogStream;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LogStream);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
