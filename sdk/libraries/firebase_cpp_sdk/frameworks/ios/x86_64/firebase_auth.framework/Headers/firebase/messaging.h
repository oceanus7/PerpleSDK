// Copyright 2016 Google Inc. All rights reserved.

#ifndef FIREBASE_MESSAGING_CLIENT_CPP_INCLUDE_FIREBASE_MESSAGING_H_
#define FIREBASE_MESSAGING_CLIENT_CPP_INCLUDE_FIREBASE_MESSAGING_H_

#include <stdint.h>
#include <map>
#include <string>
#include <vector>
#include "firebase/app.h"

namespace firebase {

/// @brief Firebase Cloud Messaging API.
///
/// Firebase Cloud Messaging allows you to send data from your server to your
/// users' devices, and receive messages from devices on the same connection.
/// The FCM service handles all aspects of queueing of messages and delivery
/// to client applications running on target devices.
namespace messaging {

/// @brief Data structure used to send messages to, and receive messages from,
/// cloud messaging.
struct Message {
  // Initialize the message.
  Message() : time_to_live(0) {}

  /// Authenticated ID of the sender. In case of D2D, it is an iid_token.
  /// This is a project number in most cases.
  std::string from;

  /// This parameter specifies the recipient of a message.
  ///
  /// For example it can be a registration token, a topic name, a IID or project
  /// ID.
  ///
  /// For upstream messages, PROJECT_ID@gcm.googleapis.com or the more general
  /// IID format are accepted.
  std::string to;

  /// The collapse key used for collapsible messages.
  std::string collapse_key;

  /// The metadata, including all original key/value pairs. Includes some of the
  /// HTTP headers used when sending the message. `gcm`, `google` and `goog`
  /// prefixes are reserved for internal use.
  std::map<std::string, std::string> data;

  /// Binary payload. For webpush and non-json messages, this is the body of the
  /// request entity.
  std::string raw_data;

  /// Message ID. This can be specified by sender. Internally a hash of the
  /// message ID and other elements will be used for storage. The ID must be
  /// unique for each topic subscription - using the same ID may result in
  /// overriding the original message or duplicate delivery.
  std::string message_id;

  /// Equivalent with a content-type.
  /// CCS uses "ack", "nack" for flow control and error handling.
  /// "control" is used by CCS for connection control.
  std::string message_type;

  /// Priority level. Defines values are "normal" and "high".
  /// By default messages are sent with normal priority.
  std::string priority;

  /// Time to live, in seconds.
  int32_t time_to_live;

  /// Error code. Used in "nack" messages for CCS, and in responses from the
  /// server.
  /// See the CCS specification for the externally-supported list.
  std::string error;

  /// Human readable details about the error.
  std::string error_description;
};

/// @brief Based class used to recieve messages from Firebase Cloud Messaging.
///
/// You need to override base class methods to handle any events required by the
/// application. Methods are invoked asynchronously and may be invoked on other
/// threads.
class Listener {
 public:
  virtual ~Listener() {}

  /// Called on the client when a message arrives.
  ///
  /// @param[in] message The data describing this message.
  virtual void OnMessage(const Message& message) = 0;

  /// Called on the client when a registration token arrives. This function
  /// will eventually be called in response to a call to
  /// firebase::messaging::Initialize(...).
  ///
  /// @param[in] token The registration token.
  virtual void OnTokenReceived(const char* token) = 0;
};

/// @brief Initialize Firebase Cloud Messaging.
///
/// After Initialize is called, the implementation may call functions on the
/// Listener provided at any time.
///
/// @param[in] app The Firebase App object for this application.
/// @param[in] listener A Listener object that listens for events from the
///            Firebase Cloud Messaging servers.
void Initialize(const App& app, Listener* listener);

/// @brief Terminate Firebase Cloud Messaging.
///
/// Frees resources associated with Firebase Cloud Messaging.
///
/// @note On Android, the services will not be shut down by this method.
void Terminate();

/// Send an upstream ("device to cloud") message. You can only use the upstream
/// feature if your FCM implementation uses the XMPP-based Cloud Connection
/// Server. The current limits for max storage time and number of outstanding
/// messages per application are documented in the [FCM Developers Guide].
///
/// [FCM Developers Guide]: https://developers.google.com/cloud-messaging/
///
/// @param[in] message The message to send upstream.
void Send(const Message& message);

/// @brief Subscribe to receive all messages to the specified topic.
///
/// Subscribes an app instance to a topic, enabling it to receive messages
/// sent to that topic.
///
/// Call this function from the main thread. FCM is not thread safe.
///
/// @param topic The topic to subscribe to. Should be of the form
///              `"/topics/<topic-name>"`.
void Subscribe(const char* topic);

/// @brief Unsubscribe from a topic.
///
/// Unsubscribes an app instance from a topic, stopping it from receiving
/// any further messages sent to that topic.
///
/// Call this function from the main thread. FCM is not thread safe.
///
/// @param topic The topic to unsubscribe from.
void Unsubscribe(const char* topic);

}  // namespace messaging
}  // namespace firebase

#endif  // FIREBASE_MESSAGING_CLIENT_CPP_INCLUDE_FIREBASE_MESSAGING_H_
