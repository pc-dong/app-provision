import DoneCallback = jest.DoneCallback;

process.env.BASE_URL = 'http://localhost:3001/api';
const errorMessages = [] as string[]
jest.mock('element-plus', () => ({
  ElMessage: {
    error: jest.fn((message: string) => errorMessages.push(message))
  }
}));
import instance, {ERROR_MESSAGE, TIMEOUT_ERROR_MESSAGE} from "./axios";
import nock from "nock";
const baseUrl: string = process.env.BASE_URL;

describe('Axios', () => {
  it('should create an axios instance', () => {
    const axiosInstance = instance;
    expect(axiosInstance).toBeDefined();
  })

  it.each(Object.keys(ERROR_MESSAGE))('should alert error message', async (status: any) => {
    nock(baseUrl)
        .get('/v1/health')
        .reply(Number(status), {message: 'Internal Server Error'});

    // verify throw error
    await expect(instance.get('/v1/health')).rejects.toThrow(Error);
    // verify alert message
    const message = ERROR_MESSAGE[status as keyof typeof ERROR_MESSAGE];
    expect(errorMessages[errorMessages.length - 1]).toEqual(message);
  });

  it('should alert timeout error message', async () => {
    nock(baseUrl)
        .get('/v1/health')
        .delay(1000)
        .reply(500, {message: 'Internal Server Error'});

    // verify throw error
    await expect(instance.get('/v1/health', {timeout: 500})).rejects.toThrow(Error);
    // verify alert message
    expect(errorMessages[errorMessages.length - 1]).toEqual(TIMEOUT_ERROR_MESSAGE);
  });
})
