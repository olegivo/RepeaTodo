//
//  NavigationHandler.swift
//  iosApp
//
//  Created by Олег Иващенко on 16.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Combine
import SwiftUI
import shared

struct NavigationHandler: ViewModifier {
    @StateObject
    var navigator: MainNavigatorObservableObject
    var onDismiss: ((NavigationDestination) -> Void)?
    @State
    private var destination: NavigationDestination?
    @State
    private var sheetActive = false
    @State
    private var linkActive = false
    @Environment(\.presentationMode) var presentation
    let viewFactory = ViewFactory()
    
    func body(content: Content) -> some View {
        content
            .background(
                EmptyView()
                    .sheet(isPresented: $sheetActive, onDismiss: {
                        if let destination = destination {
                            onDismiss?(destination)
                        }
                    }) {
                        buildDestination(destination)
                    }
            )
            .background(
                NavigationLink(destination: buildDestination(destination), isActive: $linkActive) {
                    EmptyView()
                }
            )
            .onChange(of: navigator.navigationDirection, perform: { direction in
                switch direction {
                case .forward(let destination, let style):
                    self.destination = destination
                    switch style {
                    case .present:
                        sheetActive = true
                    case .push:
                        linkActive = true
                    }
                case .back:
                    presentation.wrappedValue.dismiss()
                case .none:
                    break
                }
                navigator.navigationDirection = nil
            })
    }

    @ViewBuilder
    private func buildDestination(_ destination: NavigationDestination?) -> some View {
        if let destination = destination {
            viewFactory.makeView(destination)
        } else {
            EmptyView()
        }
    }
}

extension View {
    func handleNavigation(
        _ navigator: MainNavigatorObservableObject,
        onDismiss: ((NavigationDestination) -> Void)? = nil) -> some View {
            self.modifier(NavigationHandler(navigator: navigator, onDismiss: onDismiss))
        }
}
