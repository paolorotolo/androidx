/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.kruth

import androidx.kruth.Fact.Companion.fact

/**
 * Propositions for [Comparable] typed subjects.
 *
 * @param T the type of the object being tested by this [ComparableSubject]
 */
expect open class ComparableSubject<T : Comparable<T>> internal constructor(
    actual: T?,
    metadata: FailureMetadata = FailureMetadata(),
) : Subject<T> {

    /**
     * Checks that the subject is equivalent to [other] according to [Comparable.compareTo],
     * (i.e., checks that `a.comparesTo(b) == 0`).
     *
     * **Note:** Do not use this method for checking object equality. Instead, use [isEqualTo].
     */
    open fun isEquivalentAccordingToCompareTo(other: T?)

    /**
     * Checks that the subject is greater than [other].
     *
     * To check that the subject is greater than *or equal to* [other], use [isAtLeast].
     */
    fun isGreaterThan(other: T?)

    /**
     * Checks that the subject is less than [other].
     *
     * @throws NullPointerException if [actual] or [other] is `null`.
     */
    fun isLessThan(other: T?)

    /**
     * Checks that the subject is less than or equal to [other].
     *
     * @throws NullPointerException if [actual] or [other] is `null`.
     */
    fun isAtMost(other: T?)

    /**
     * Checks that the subject is greater than or equal to [other].
     *
     * @throws NullPointerException if [actual] or [other] is `null`.
     */
    fun isAtLeast(other: T?)
}

// TODO(KT-20427): Internal helpers to share impl between actuals, since there is not yet a way
//  to share default impls from an expect class.

internal fun <T : Comparable<T>> ComparableSubject<T>.isInImpl(range: ClosedRange<T>) {
    if (requireNonNull(actual) !in range) {
        failWithoutActualInternal(fact("Expected to be in range", range))
    }
}

internal fun <T : Comparable<T>> ComparableSubject<T>.isNotInImpl(range: ClosedRange<T>) {
    if (requireNonNull(actual) in range) {
        failWithoutActualInternal(fact("Expected not to be in range", range))
    }
}

internal fun <T : Comparable<T>> ComparableSubject<T>.isEquivalentAccordingToCompareToImpl(
    other: T?
) {
    requireNonNull(actual)
    requireNonNull(other)

    if (actual.compareTo(other) != 0) {
        failWithActualInternal(fact("Expected value that sorts equal to", other))
    }
}

internal fun <T : Comparable<T>> ComparableSubject<T>.isGreaterThanImpl(other: T?) {
    requireNonNull(actual)
    requireNonNull(other)

    if (actual <= other) {
        failWithActualInternal(fact("Expected to be greater than", other))
    }
}

internal fun <T : Comparable<T>> ComparableSubject<T>.isLessThanImpl(other: T?) {
    requireNonNull(actual) { "Expected to be less than $other, but was $actual" }
    requireNonNull(other) { "Expected to be less than $other, but was $actual" }

    if (actual >= other) {
        failWithActualInternal(fact("Expected to be less than", other))
    }
}

internal fun <T : Comparable<T>> ComparableSubject<T>.isAtMostImpl(other: T?) {
    requireNonNull(actual) { "Expected to be at most $other, but was $actual" }
    requireNonNull(other) { "Expected to be at most $other, but was $actual" }
    if (actual > other) {
        failWithActualInternal(fact("Expected to be at most", other))
    }
}

internal fun <T : Comparable<T>> ComparableSubject<T>.isAtLeastImpl(other: T?) {
    requireNonNull(actual) { "Expected to be at least $other, but was $actual" }
    requireNonNull(other) { "Expected to be at least $other, but was $actual" }
    if (actual < other) {
        failWithActualInternal(fact("Expected to be at least", other))
    }
}
