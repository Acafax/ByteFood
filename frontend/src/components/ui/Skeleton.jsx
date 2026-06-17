import React from 'react';

/**
 * Base shimmer block used to compose skeleton screens.
 * Mirrors the shape/size of the content it stands in for so the layout
 * does not shift once real data arrives.
 */
export function Skeleton({ className = '' }) {
  return (
    <div
      className={`animate-pulse bg-slate-200 rounded ${className}`}
      aria-hidden="true"
    />
  );
}

/**
 * Skeleton for the "create-*" form pages (product, modification, combo,
 * semi-product). Reproduces the card header, a row of input fields and the
 * grid of selectable item cards shown while the underlying data is fetched.
 */
export function FormCardSkeleton({ fields = 3, cards = 6 }) {
  return (
    <div
      className="bg-white rounded-xl border border-gray-200 shadow-card p-8"
      aria-busy="true"
    >
      <div className="mb-6 space-y-2">
        <Skeleton className="h-7 w-64" />
        <Skeleton className="h-4 w-80" />
      </div>

      <div className="space-y-8">
        <section>
          <Skeleton className="h-5 w-40 mb-4" />
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {Array.from({ length: fields }).map((_, i) => (
              <div key={i} className="space-y-2">
                <Skeleton className="h-4 w-24" />
                <Skeleton className="h-11 w-full rounded-lg" />
              </div>
            ))}
          </div>
        </section>

        <section>
          <Skeleton className="h-6 w-56 mb-2" />
          <Skeleton className="h-4 w-72 mb-4" />
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {Array.from({ length: cards }).map((_, i) => (
              <div
                key={i}
                className="rounded-xl border border-slate-200 bg-slate-50 p-4 space-y-3"
              >
                <Skeleton className="h-4 w-32" />
                <Skeleton className="h-12 w-full rounded-lg" />
              </div>
            ))}
          </div>
        </section>

        <div className="flex justify-end gap-4 pt-4 border-t border-gray-200">
          <Skeleton className="h-11 w-40 rounded-lg" />
          <Skeleton className="h-11 w-44 rounded-lg" />
        </div>
      </div>
    </div>
  );
}

/**
 * Skeleton for the stock (Magazyn) page: the three summary cards plus the
 * inventory table rows.
 */
export function StockTableSkeleton({ rows = 6 }) {
  return (
    <div className="space-y-6" aria-busy="true">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {Array.from({ length: 3 }).map((_, i) => (
          <div
            key={i}
            className="bg-white rounded-xl border border-gray-200 shadow-sm p-6 border-l-4 border-slate-200"
          >
            <div className="flex items-center justify-between">
              <div className="space-y-2">
                <Skeleton className="h-4 w-28" />
                <Skeleton className="h-8 w-16" />
              </div>
              <Skeleton className="h-10 w-10 rounded-full" />
            </div>
          </div>
        ))}
      </div>

      <div className="bg-white rounded-xl border border-gray-200 shadow-card overflow-hidden">
        <div className="bg-gradient-to-r from-[#FF6600] to-orange-500 px-8 py-5">
          <Skeleton className="h-6 w-48 bg-white/40" />
        </div>
        <div className="divide-y divide-gray-200">
          {Array.from({ length: rows }).map((_, i) => (
            <div key={i} className="flex items-center justify-between px-8 py-4">
              <Skeleton className="h-4 w-40" />
              <Skeleton className="h-4 w-20" />
              <Skeleton className="h-4 w-20" />
              <Skeleton className="h-6 w-24 rounded-full" />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default Skeleton;
