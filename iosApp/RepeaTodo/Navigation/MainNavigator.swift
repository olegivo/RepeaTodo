//
//  MainNavigator.swift
//  iosApp
//
//  Created by o.ivaschenko on 16.04.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import Combine
import shared
import SwiftUI

public extension MainNavigator {
    internal func asObservableObject() -> MainNavigatorObservableObject {
        return MainNavigatorObservableObject(wrapped: self)
    }
}

class MainNavigatorObservableObject: ObservableObject {
    var wrapped: MainNavigator
    @Published
    var navigationDirection: NavigationDirection? = nil

    init(wrapped: MainNavigator) {
        self.wrapped = wrapped
        
        let back = createPublisher(wrapped.navigationBack)
            .map({ navigationDestination in
                Optional<NavigationDirection>(
                    NavigationDirection.back
                )
            })
        
        createPublisher(wrapped.navigationDestination)
            .map({ navigationDestination in
                Optional<NavigationDirection>(
                    NavigationDirection.forward(destination: navigationDestination, style: .present)
                )
            })
            .merge(with: back)
            .receive(on: RunLoop.main)
            .assign(to: &$navigationDirection)
    }
}
