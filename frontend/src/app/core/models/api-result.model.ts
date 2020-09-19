export class APIResult {
  ok: boolean;
  data: any;
  error: boolean;
  errorMessage: string;

  private constructor(ok: boolean, data: any, error: boolean, errorMessage: string) {
    this.ok = ok;
    this.data = data || null;
    this.error = error;
    this.errorMessage = errorMessage || null;
  }
  static ok(data?: any) {
    return new APIResult(true, data, false, null);
  }
  static error(message: string) {
    return new APIResult(false, null, true, message == 'No message available' ? 'An error occurred.' : message);
  }
}