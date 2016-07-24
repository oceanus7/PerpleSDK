// Copyright 2016 Google Inc. All Rights Reserved.

#ifndef FIREBASE_APP_CLIENT_CPP_INCLUDE_FIREBASE_FUTURE_H_
#define FIREBASE_APP_CLIENT_CPP_INCLUDE_FIREBASE_FUTURE_H_

#include <stddef.h>
#include <stdint.h>
#include <utility>

// TODO(amaurice): Move to a more central header file
// Move operators use rvalue references, which are a C++11 extension.
// Also, stlport doesn't implement std::move().
#if __cplusplus >= 201103L && !defined(_STLPORT_VERSION)
#define FIREBASE_USE_MOVE_OPERATORS
#endif  // __cplusplus >= 201103L && !defined(_STLPORT_VERSION)

namespace firebase {

// Predeclarations.
/// @cond FIREBASE_APP_INTERNAL
namespace detail {
class FutureApiInterface;
}  // namespace detail
/// @endcond

/// Asynchronous call status.
enum FutureStatus {
  /// Results are ready.
  kFutureStatusComplete,

  /// Result is still being processed.
  kFutureStatusPending,

  /// No result is pending.
  /// FutureBase::Release() or move operator was called.
  kFutureStatusInvalid,
};

/// Handle that the API uses to identify an asynchronous call.
/// The exact interpretation of the handle is up to the API.
typedef uintptr_t FutureHandle;

/// Type-independent return type of asynchronous calls.
///
/// @cond FIREBASE_APP_INTERNAL
/// Notes:
///   - Futures have pointers back to the API, but the API does not maintain
///     pointers to its Futures. Therefore, all Futures must be destroyed
///     *before* the API is destroyed.
///   - Futures can be moved or copied. Call results are reference counted,
///     and are destroyed when they are long longer referenced by any Futures.
///   - The actual `Status`, `Error`, and `Result` values are kept inside the
///     API. This makes synchronization and data management easier.
///
/// WARNING: This class should remain POD (plain old data). It should not have
///          virtual methods. Nor should the derived Future<T> class add any
///          data. Internally, we static_cast FutureBase to Future<T>,
///          so the underlying data should remain the same.
/// @endcond
class FutureBase {
 public:
  /// Function pointer for a completion callback. When we call this, we will
  /// send the completed future, along with the user data that you specified
  /// when you set up the callback.
  typedef void (*CompletionCallback)(const FutureBase& result_data,
                                     void* user_data);

  /// Construct an untyped future.
  FutureBase();

  /// @cond FIREBASE_APP_INTERNAL

  /// Construct an untyped future using the specified API and handle.
  ///
  /// @param api API class used to provide the future implementation.
  /// @param handle Handle to the future.
  FutureBase(detail::FutureApiInterface* api, FutureHandle handle);

  /// @endcond

  ~FutureBase();

  /// Copy constructor and operator.
  /// Increment the reference count when creating a copy of the future.
  FutureBase(const FutureBase& rhs);

  /// Copy an untyped future.
  FutureBase& operator=(const FutureBase& rhs);

#if defined(FIREBASE_USE_MOVE_OPERATORS)
  /// Move constructor and operator.
  /// Move is more efficient than copy and delete because we don't touch the
  /// reference counting in the API.
  FutureBase(FutureBase&& rhs);

  /// Copy an untyped future.
  FutureBase& operator=(FutureBase&& rhs);
#endif  // defined(FIREBASE_USE_MOVE_OPERATORS)

  /// Explicitly release the internal resources for a future.
  /// Future will become invalid.
  void Release();

  /// Completion status of the asynchronous call.
  FutureStatus Status() const;

  /// When Status() is kFutureStatusComplete, returns the API-defined
  /// error code. Otherwise, return value is undefined.
  int Error() const;

  /// When Status() is kFutureStatusComplete, returns the API-defined error
  /// message, as human-readable text, or an empty string if the API does not
  /// provide a human readable description of the error.
  ///
  /// @note The returned pointer is only valid for the lifetime of the Future
  ///       or its copies.
  const char* ErrorMessage() const;

  /// Result of the asynchronous call, or nullptr if the result is still
  /// pending. Cast is required since GetFutureResult() returns void*.
  const void* ResultVoid() const;

  /// Register a callback that will be called at most once, when the future is
  /// completed.
  ///
  /// If you call OnCompletion() itself more than once, only the most
  /// recent callback you registered be called.
  ///
  /// When your callback is called, the user_data that you supplied here will be
  /// passed back as the second parameter.
  ///
  /// @param[in] callback Function pointer to your callback.
  /// @param[in] user_data Optional user data. We will pass this back to your
  /// callback.
  void OnCompletion(CompletionCallback callback, void* user_data) const;

 protected:
  /// @cond FIREBASE_APP_INTERNAL

  /// Backpointer to the issuing API class.
  /// Set to nullptr when Future is invalidated.
  detail::FutureApiInterface* api_;

  /// API-specified handle type.
  FutureHandle handle_;

  /// @endcond
};

/// Type-specific version of FutureBase.
///   @tparam ResultType the type returned by the call to Api.
//
// WARNING: This class should not have virtual methods or data members.
//          See the warning in FutureBase for further details.
template <typename ResultType>
class Future : public FutureBase {
 public:
  /// Function pointer for a completion callback. When we call this, we will
  /// send the completed future, along with the user data that you specified
  /// when you set up the callback.
  typedef void (*TypedCompletionCallback)(const Future<ResultType>& result_data,
                                          void* user_data);

  /// Construct a future.
  Future() {}

  /// @cond FIREBASE_APP_INTERNAL

  /// Construct a future using the specified API and handle.
  ///
  /// @param api API class used to provide the future implementation.
  /// @param handle Handle to the future.
  Future(detail::FutureApiInterface* api, FutureHandle handle)
      : FutureBase(api, handle) {}

  /// @endcond

  /// Result of the asynchronous call, or nullptr if the result is still
  /// pending. Allows the API to provide a type-specific interface.
  const ResultType* Result() const {
    return static_cast<const ResultType*>(ResultVoid());
  }

  /// Register a callback that will be called at most once, when the future is
  /// completed.
  ///
  /// If you call OnCompletion() itself more than once, only the most recent
  /// callback you registered will be called.
  ///
  /// When your callback is called, the user_data that you supplied here will be
  /// passed back as the second parameter.
  ///
  /// @param[in] callback Function pointer to your callback.
  /// @param[in] user_data Optional user data. We will pass this back to your
  /// callback.
  ///
  /// @note This is the same callback as FutureBase::OnCompletion(), so you
  /// can't expect to set both and have both run; again, only the most recently
  /// registered one will run.
  void OnCompletion(TypedCompletionCallback callback, void* user_data) const {
    FutureBase::OnCompletion(reinterpret_cast<CompletionCallback>(callback),
                             user_data);
  }
};

}  // namespace firebase

// Include the inline implementation.
#include "firebase/internal/future_impl.h"

#endif  // FIREBASE_APP_CLIENT_CPP_INCLUDE_FIREBASE_FUTURE_H_
