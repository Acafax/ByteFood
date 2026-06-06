import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { get, post, setToken } from '../api/client.js';
import { saveAuthToStorage, clearAuthStorage } from './authStorage.js';

/**
 * @typedef {'EMPLOYEE' | 'MANAGER' | 'ADMIN'} UserRole
 */

/**
 * @typedef {Object} UserInfo
 * @property {string} email
 * @property {string} username
 */

/**
 * GET /api/auth/me (UserInfo)
 * @typedef {Object} MeResponse
 * @property {number|null} restaurantId - null if user dont have restaurant
 * @property {string} email
 * @property {string} username
 * @property {UserRole} role
 */

const initialState = {
  role: null,
  restaurantId: null,
  userInfo: null,
  isLoading: false,
  isAuthenticated: false,
};

/**
 * @param {MeResponse} data
 */
function mapMeResponseToAuth(data) {
  return {
    role: data.role,
    restaurantId: data.restaurantId ?? null,
    userInfo: {
      email: data.email,
      username: data.username,
    },
  };
}

export const fetchCurrentUser = createAsyncThunk(
  'auth/fetchCurrentUser',
  async (_, { rejectWithValue }) => {
    try {
      /** @type {MeResponse} */
      const data = await get('/auth/me');
      return mapMeResponseToAuth(data);
    } catch (error) {
      if (error.status === 409) {
        return rejectWithValue({
          code: 'ONBOARDING_REQUIRED',
          message: error.message,
        });
      }
      return rejectWithValue({
        code: 'AUTH_FAILED',
        message: error.message || 'Nie udało się pobrać danych użytkownika',
      });
    }
  },
);

export const loginUser = createAsyncThunk(
  'auth/loginUser',
  async ({ email, password }, { dispatch, rejectWithValue }) => {
    try {
      const response = await post('/auth/login', { email, password });
      setToken(response.token);
      await dispatch(fetchCurrentUser()).unwrap();
      return response;
    } catch (error) {
      clearAuthStorage();
      const message = error.data?.message || error.message || 'Logowanie nie powiodło się';
      return rejectWithValue(message);
    }
  },
);

export const registerUser = createAsyncThunk(
  'auth/registerUser',
  async ({ email, password, name, lastName }, { dispatch, rejectWithValue }) => {
    try {
      const response = await post('/auth/register', { email, password, name, lastName });
      setToken(response.token);
      await dispatch(fetchCurrentUser()).unwrap();
      return response;
    } catch (error) {
      clearAuthStorage();
      const message = error.data?.message || error.message || 'Rejestracja nie powiodła się';
      return rejectWithValue(message);
    }
  },
);

export const logoutUser = createAsyncThunk('auth/logoutUser', async () => {
  clearAuthStorage();
});

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    clearAuth: () => initialState,
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCurrentUser.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(fetchCurrentUser.fulfilled, (state, action) => {
        state.role = action.payload.role;
        state.restaurantId = action.payload.restaurantId;
        state.userInfo = action.payload.userInfo;
        state.isAuthenticated = true;
        state.isLoading = false;
        saveAuthToStorage(action.payload);
      })
      .addCase(fetchCurrentUser.rejected, (state, action) => {
        if (action.payload?.code === 'ONBOARDING_REQUIRED') {
          state.role = 'MANAGER';
          state.restaurantId = null;
          state.userInfo = null;
          state.isAuthenticated = true;
          state.isLoading = false;
          return;
        }
        clearAuthStorage();
        Object.assign(state, initialState);
      })
      .addCase(loginUser.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(loginUser.fulfilled, (state) => {
        state.isLoading = false;
      })
      .addCase(loginUser.rejected, (state) => {
        Object.assign(state, initialState);
      })
      .addCase(registerUser.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(registerUser.fulfilled, (state) => {
        state.isLoading = false;
      })
      .addCase(registerUser.rejected, (state) => {
        Object.assign(state, initialState);
      })
      .addCase(logoutUser.fulfilled, (state) => {
        Object.assign(state, initialState);
      });
  },
});

export const { clearAuth } = authSlice.actions;
export default authSlice.reducer;
