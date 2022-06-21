//
//  EditTaskViewModel.swift
//  iosApp
//
//  Created by Олег Иващенко on 21.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import Combine
import shared

public class EditTaskViewModelObservableObject : ObservableObject {
    @Published
    var title: String
    
    @Published
    var isLoading:Bool
    
    @Published
    var canSave:Bool
    
    @Published
    var navigationDirection: NavigationDirection?
    
    init() {
        title = ""
        isLoading = false
        canSave = false
    }
    
    func onSaveClicked() {
    }
    
    func onCancelClicked() {
    }
}
