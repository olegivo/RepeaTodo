//
//  PreviewEnvironmentExt.swift
//  iosApp
//
//  Created by o.ivaschenko on 22.04.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import shared

func preview(using closure: (PreviewEnvironment) -> Void) -> PreviewEnvironment {
    let previewEnvironment = PreviewEnvironment()
    closure(previewEnvironment)
    return previewEnvironment
}

extension PreviewEnvironment {
    func get<T: AnyObject>() -> T {
        guard let result = get(objCClass: T.self) as? T else {
            fatalError("Can't provide an instance of type: \(T.self)")
        }

        return result
    }
}
