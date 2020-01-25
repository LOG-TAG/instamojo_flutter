#import "InstamojoFlutterPlugin.h"
#if __has_include(<instamojo_flutter/instamojo_flutter-Swift.h>)
#import <instamojo_flutter/instamojo_flutter-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "instamojo_flutter-Swift.h"
#endif

@implementation InstamojoFlutterPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftInstamojoFlutterPlugin registerWithRegistrar:registrar];
}
@end
