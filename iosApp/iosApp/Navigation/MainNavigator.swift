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
    var navigationDirection: NavigationDirection?

    var navigationDirection1: NavigationDirection? = nil

    
    init(wrapped: MainNavigator) {
        self.wrapped = wrapped
        
        let back = (wrapped.navigationBack.asPublisher() as AnyPublisher<KotlinUnit, Never>)
            .map({ navigationDestination in
                Optional<NavigationDirection>(NavigationDirection.back)
            })
        
        (wrapped.navigationDestination.asPublisher() as AnyPublisher<NavigationDestination?, Never>)
            .map({ navigationDestination in
                guard let navigationDestination = navigationDestination else { return nil }
                return NavigationDirection.forward(destination: navigationDestination, style: .present)
            })
            .merge(with: back)
            .receive(on: RunLoop.main)
            .assign(to: &$navigationDirection)
    }
}
