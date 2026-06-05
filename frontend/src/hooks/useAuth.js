import { useDispatch, useSelector } from 'react-redux';
import { loginUser, registerUser, logoutUser } from '../store/authSlice.js';

export function useAuth() {
  const dispatch = useDispatch();
  const auth = useSelector((state) => state.auth);

  const user = auth.userInfo
    ? { ...auth.userInfo, name: auth.userInfo.username}
    : null;

  return {
    ...auth,
    user,
    loading: auth.isLoading,
    login: (email, password) =>
      dispatch(loginUser({ email, password })).unwrap(),
    register: (email, password, name, lastName) =>
      dispatch(registerUser({ email, password, name, lastName })).unwrap(),
    logout: () => dispatch(logoutUser()),
  };
}
