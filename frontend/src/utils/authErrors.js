/**
 * Maps an authentication error (thrown by the login/register thunks) to a
 * user-friendly, localized message.
 *
 * The auth thunks reject with a plain string payload (see authSlice.js), but
 * this helper also accepts Error objects / API error shapes so it can be used
 * defensively from any caller.
 *
 * The "email already exists" case is handled explicitly and separately from
 * generic failures, because the backend returns a raw English message
 * (e.g. `User with email: jan@x.pl already exist`) that should not be shown to
 * end users.
 *
 * @param {unknown} err - The caught error (string payload, Error, or API error).
 * @param {string} fallback - Generic message used when no specific case matches.
 * @returns {string}
 */
export function getAuthErrorMessage(err, fallback) {
  const raw =
    typeof err === 'string'
      ? err
      : err?.data?.message || err?.message || '';

  if (/already\s*exist/i.test(raw)) {
    return 'Konto z tym adresem e-mail już istnieje.';
  }

  return fallback;
}
