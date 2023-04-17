//
//  EnvironmentValues.swift
//  iosApp
//
//  Created by o.ivaschenko on 16.04.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

private struct NavigatorKey: EnvironmentKey {
    static let defaultValue: MainNavigatorObservableObject = MainNavigatorObservableObject(wrapped: FakeMainNavigator())
}

extension EnvironmentValues {
    var navigator: MainNavigatorObservableObject {
            get { self[NavigatorKey.self] }
            set { self[NavigatorKey.self] = newValue }
        }
}

extension View {
    func navigator(_ navigator: MainNavigatorObservableObject) -> some View {
        environment(\.navigator, navigator)
    }
}
